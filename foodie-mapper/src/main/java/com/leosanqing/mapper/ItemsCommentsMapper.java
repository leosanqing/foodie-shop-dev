package com.leosanqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leosanqing.pojo.ItemsComments;
import com.leosanqing.pojo.vo.MyCommentVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.Map;

/**
 * @author zhuerchong
 */
public interface ItemsCommentsMapper extends BaseMapper<ItemsComments> {
    /**
     * 保存评价列表
     *
     * @param map
     */
    void saveComments(Map<String, Object> map);

    /**
     * 查询我的评价
     *
     * @param userId
     * @param page
     * @return
     */
    IPage<MyCommentVO> queryMyComments(@Param("userId") String userId, Page<MyCommentVO> page);
}