package com.leosanqing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.mapper.CategoryMapper;
import com.leosanqing.pojo.Category;
import com.leosanqing.pojo.vo.CategoryVO;
import com.leosanqing.pojo.vo.NewItemsVO;
import com.leosanqing.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: leosanqing
 * @Date: 2019-12-07 23:15
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {
        return lambdaQuery()
                .eq(Category::getType, 1)
                .list();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return baseMapper.getSubCatList(rootCatId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {
        return baseMapper.getSixNewItemsLazy(rootCatId);
    }


}
