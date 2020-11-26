package com.leosanqing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.mapper.CarouselMapper;
import com.leosanqing.pojo.Carousel;
import com.leosanqing.service.CarouselService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: leosanqing
 * @Date: 2020/5/6 上午10:58
 * @Package: com.leosanqing.index.service.impl
 * @Description: 首页服务实现类
 * @Version: 1.0
 */
@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Carousel> queryAll(Integer isShow) {
        return lambdaQuery()
                .eq(Carousel::getIsShow, isShow)
                .orderByAsc(Carousel::getSort)
                .list();
    }
}
