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
     *
     * @param githubUser
     * @param response
     * @return
     */
    @Override
    public String save(GithubUser githubUser, HttpServletResponse response) {
        if (githubUser != null && githubUser.getId() != null){
            //登录成功, 将用户信息装入数据库
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userMapper.insert(user);
            //将生成的token装入cookie中
            response.addCookie(new Cookie("token",token));
            return "loginSuccess";
        } else {
            //登录失败,重新登录
            return "loginFail";
        }
    }


}
