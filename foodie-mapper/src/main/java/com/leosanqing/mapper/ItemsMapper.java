package com.leosanqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leosanqing.pojo.Items;
import com.leosanqing.pojo.vo.ItemCommentVO;
import com.leosanqing.pojo.vo.SearchItemsVO;
import com.leosanqing.pojo.vo.ShopcartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zhuerchong
 */
public interface ItemsMapper extends BaseMapper<Items> {
    /**
     * 查询商品评价
     *
     * @param map
     * @return
     */
    IPage<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map,
                                           @Param("page") Page<ItemCommentVO> page);


    /**
     * 根据关键字查询商品
     *
     * @param map
     * @param page
     * @return
     */
    IPage<Items> searchItems(@Param("paramsMap") Map<String, String> map, @Param("page") Page<Items> page);


    /**
     * 根据第三级目录查询商品
     *
     * @param map
     * @return
     */
    IPage<SearchItemsVO> searchItemsByThirdCatId(@Param("paramsMap") Map<String, Object> map,
                                                 @Param("page") Page<SearchItemsVO> page);

    /**
     * 减库存
     *
     * @param pendingCount
     * @param specId
     * @return
     */
    Integer decreaseItemSpecStock(@Param("itemSpecId") String specId, @Param("pendingCount") Integer pendingCount);


    /**
     * 根据第三级目录查询商品
     *
     * @param specIdsList
     * @return
     */
    List<ShopcartVO> queryItemsBySpecIds(@Param("paramsList") List<String> specIdsList);

}