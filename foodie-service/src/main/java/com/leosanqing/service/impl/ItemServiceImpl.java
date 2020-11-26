package com.leosanqing.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.enums.YesOrNo;
import com.leosanqing.mapper.*;
import com.leosanqing.pojo.*;
import com.leosanqing.pojo.vo.CommentLevelCountsVO;
import com.leosanqing.pojo.vo.ItemCommentVO;
import com.leosanqing.pojo.vo.SearchItemsVO;
import com.leosanqing.pojo.vo.ShopcartVO;
import com.leosanqing.service.ItemService;
import com.leosanqing.utils.DesensitizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: leosanqing
 * @Date: 2019-12-08 20:49
 */
@Service
@Slf4j
public class ItemServiceImpl extends ServiceImpl<ItemsMapper, Items> implements ItemService {


    @Resource
    private ItemsImgMapper itemsImgMapper;

    @Resource
    private ItemsSpecMapper itemsSpecMapper;

    @Resource
    private ItemsParamMapper itemsParamMapper;

    @Resource
    private ItemsCommentsMapper itemsCommentsMapper;

    @Resource
    private RedissonClient redisson;

    @Override
    public Items queryItemsById(String itemId) {
        return baseMapper.selectById(itemId);
    }

    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        return itemsImgMapper.selectList(
                Wrappers
                        .lambdaQuery(ItemsImg.class)
                        .eq(ItemsImg::getItemId, itemId)
        );
    }

    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        return itemsSpecMapper.selectList(
                Wrappers.lambdaQuery(ItemsSpec.class)
                        .eq(ItemsSpec::getItemId, itemId)
        );
    }

    @Override
    public ItemsParam queryItemParam(String itemId) {
        return itemsParamMapper.selectOne(
                Wrappers.lambdaQuery(ItemsParam.class)
                        .eq(ItemsParam::getItemId, itemId)
        );
    }

    public enum CommentLevel {
        /**
         * 表示评价等级的枚举
         */
        GOOD(1, "好评"),
        NORMAL(2, "中评"),
        BAD(3, "差评");

        public final int type;
        public final String value;

        CommentLevel(int type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        Integer good = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normal = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer bad = getCommentCounts(itemId, CommentLevel.BAD.type);
        Integer total = good + bad + normal;

        return new CommentLevelCountsVO(total, good, normal, bad);
    }

    @Override
    public IPage<ItemCommentVO> queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("itemId", itemId);
        map.put("level", level);

        IPage<ItemCommentVO> comments = baseMapper.queryItemComments(map, new Page<>(page, pageSize));

        // 进行脱敏处理
        for (ItemCommentVO itemCommentVO : comments.getRecords()) {
            itemCommentVO.setNickname(DesensitizationUtil.commonDisplay(itemCommentVO.getNickname()));
        }

        return comments;
    }

    @Override
    public IPage<Items> searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, String> map = new HashMap<>(8);
        map.put("keywords", keywords);
        map.put("sort", sort);
        return baseMapper.searchItems(map, new Page<>(page, pageSize));
    }

    @Override
    public IPage<SearchItemsVO> searchItemsByCatId(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("catId", catId);
        map.put("sort", sort);

        return baseMapper.searchItemsByThirdCatId(map, new Page<>(page, pageSize));
    }

//    private PagedGridResult setterPage(List<?> list, int page) {
//        PageInfo<?> pageList = new PageInfo<>(list);
//        PagedGridResult grid = new PagedGridResult();
//        grid.setPage(page);
//        grid.setRows(list);
//        grid.setTotal(pageList.getPages());
//        grid.setRecords(pageList.getTotal());
//        return grid;
//    }

    private Integer getCommentCounts(String itemId, Integer level) {
        return itemsCommentsMapper.selectCount(
                Wrappers.lambdaQuery(ItemsComments.class)
                        .eq(ItemsComments::getCommentLevel, level)
                        .eq(ItemsComments::getItemId, itemId)
        );
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void decreaseItemSpecStock(String specId, Integer buyCount) {
        /**
         *  分布式锁【3】 编写业务代码
         *  1、Redisson是基于Redis，使用Redisson之前，项目必须使用Redis
         *   2、注意getLock方法中的参数，以specId作为参数，每个specId一个key，和
         *   数据库中的行锁是一致的，不会是方法级别的锁
         */
        RLock rLock = redisson.getLock("SPECID_" + specId);
        try {
            /**
             * 1、获取分布式锁，锁的超时时间是5秒get
             *  2、获取到了锁，进行后续的业务操作
             */
            rLock.lock(5, TimeUnit.HOURS);

            int result = baseMapper.decreaseItemSpecStock(specId, buyCount);
            if (result != 1) {
                throw new RuntimeException("订单创建失败，原因：库存不足!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            /**
             *  不管业务是否操作正确，随后都要释放掉分布式锁
             *   如果不释放，过了超时时间也会自动释放
             */
            rLock.unlock();
        }

//
    }

    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {
        String[] ids = specIds.split(",");
        return baseMapper.queryItemsBySpecIds(Arrays.asList(ids));
    }

    @Override
    public ItemsSpec queryItemBySpecId(String specId) {
        return itemsSpecMapper.selectById(specId);
    }

    @Override
    public String queryItemImgByItemId(String itemId) {
        ItemsImg itemsImg = itemsImgMapper.selectOne(
                Wrappers.lambdaQuery(ItemsImg.class)
                        .eq(ItemsImg::getItemId, itemId)
                        .eq(ItemsImg::getIsMain, YesOrNo.YES.type)
        );

        return itemsImg == null ? "" : itemsImg.getUrl();
    }
}
