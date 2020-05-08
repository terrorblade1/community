package com.java.community.service;

import com.java.community.dto.GithubUser;
import com.java.community.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: yk
 * Date: 2020/5/7 7:17
 */
public interface UserService {

    String save(GithubUser githubUser, HttpServletResponse response);

    User findByToken(HttpServletRequest request);
}
