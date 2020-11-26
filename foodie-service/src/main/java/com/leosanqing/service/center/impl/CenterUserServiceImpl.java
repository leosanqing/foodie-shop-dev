package com.leosanqing.service.center.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.mapper.UsersMapper;
import com.leosanqing.pojo.Users;
import com.leosanqing.pojo.bo.center.CenterUserBO;
import com.leosanqing.service.center.CenterUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: leosanqing
 * @Date: 2019-12-15 20:05
 * @Package: com.leosanqing.service.center.impl
 * @Description: TODO
 */
@Service
public class CenterUserServiceImpl extends ServiceImpl<UsersMapper, Users> implements CenterUserService {

    @Override
    public Users queryUserInfo(String userId) {
        Users users = baseMapper.selectById(userId);
        users.setPassword(null);
        return users;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {
        final Users users = new Users();
        BeanUtils.copyProperties(centerUserBO, users);
        users.setId(userId);
        users.setUpdatedTime(new Date());
        baseMapper.updateById(users);

        return queryUserInfo(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUserFace(String userId, String faceUrl) {
        final Users users = new Users();
        users.setId(userId);
        users.setFace(faceUrl);
        users.setUpdatedTime(new Date());
        baseMapper.updateById(users);

        return queryUserInfo(userId);

    }
}
