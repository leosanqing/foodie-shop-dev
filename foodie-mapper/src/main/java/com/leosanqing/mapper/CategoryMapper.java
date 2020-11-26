package com.leosanqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leosanqing.pojo.Category;
import com.leosanqing.pojo.vo.CategoryVO;
import com.leosanqing.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 查询子分类
     *
     * @param rootCatId
     * @return
     */
    List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询每个分类下的六个商品信息
     *
     * @param rootCatId 分类Id
     * @return
     */
    List<NewItemsVO> getSixNewItemsLazy(@Param("rootCatId") Integer rootCatId);
}