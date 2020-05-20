package com.java.community.controller;

import com.java.community.dto.PaginationDTO;
import com.java.community.dto.QuestionDTO;
import com.java.community.model.User;
import com.java.community.service.QuestionService;
import com.java.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/5 18:24
 */
@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    /**
     * 显示首页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "6") Integer size){
        PaginationDTO pagination = questionService.findAll(page,size);
        List<QuestionDTO> questions = questionService.findHotQuestions();
        model.addAttribute("pagination",pagination);
        model.addAttribute("questions",questions);
        return "index";
    }

}
