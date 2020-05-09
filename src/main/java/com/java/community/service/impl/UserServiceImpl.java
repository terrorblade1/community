package com.java.community.service.impl;

import com.java.community.dto.GithubUser;
import com.java.community.mapper.UserMapper;
import com.java.community.model.User;
import com.java.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Author: yk
 * Date: 2020/5/7 7:18
 */
@Service
@Transactional(readOnly = false)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加或更新用户数据
     * @param user
     * @return
     */
    @Override
    public void saveOrModify(User user) {
        User dbUser = userMapper.selectByAccountId(user.getAccountId());
        if (dbUser == null){  //数据库中没有用户数据
            //插入用户数据
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {  //数据库中存在用户数据
            //更新用户数据
            dbUser.setGmtModified(System.currentTimeMillis());
            dbUser.setAvatarUrl(user.getAvatarUrl());
            dbUser.setName(user.getName());
            dbUser.setToken(user.getToken());
            userMapper.update(dbUser);
        }
    }

}
