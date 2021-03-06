package com.leosanqing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.enums.Sex;
import com.leosanqing.mapper.UsersMapper;
import com.leosanqing.pojo.Users;
import com.leosanqing.pojo.bo.UserBO;
import com.leosanqing.service.UserService;
import com.leosanqing.utils.DateUtil;
import com.leosanqing.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: leosanqing
 * @Date: 2019-12-06 00:16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users> implements UserService {

    private static final String FACE_PATH = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAllFXAAAclhVPdSg994" +
            ".png";

    @Resource
    private Sid sid;

    @Override
    public boolean queryUsernameIsExist(String username) {
        return lambdaQuery()
                .eq(Users::getUsername, username)
                .one() != null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users createUser(UserBO userBO) {
        Users users = null;
        try {
            users = Users.builder()
                    .id(sid.nextShort())
                    .username(userBO.getUsername())
                    .password(MD5Utils.getMD5Str(userBO.getPassword()))
                    .face(FACE_PATH)
                    .birthday(DateUtil.stringToDate("1900-01-01"))
                    .nickname(userBO.getUsername())
                    .sex(Sex.SECRET.type)
                    .createdTime(new Date())
                    .updatedTime(new Date())
                    .build();
        } catch (Exception e) {
            log.error("创建用户失败,error{}",e);
        }
        baseMapper.insert(users);
        return users;
    }


    @Override
    public Users queryUsersForLogin(String username, String password) throws Exception {
        return lambdaQuery()
                .eq(Users::getUsername, username)
                .eq(Users::getPassword, MD5Utils.getMD5Str(password))
                .one();
    }
}
