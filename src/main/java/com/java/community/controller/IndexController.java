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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping(value = {"/","/{action}"})
    public String index(@PathVariable(name = "action",required = false) String action,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "10") Integer size){
        List<QuestionDTO> questions = questionService.findHotQuestions();
        model.addAttribute("questions",questions);
        PaginationDTO pagination = null;
        if (action == null || "".equals(action) || (!"byZero".equals(action))){
            pagination = questionService.findAll(page,size);
        } else {
            pagination = questionService.findByZero(page,size);
        }
        model.addAttribute("pagination",pagination);
        return "index";
    }

    /*@RequestMapping("/byZero")
    public String zero(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "20") Integer size){
        PaginationDTO pagination = questionService.findByZero(page,size);
        List<QuestionDTO> questions = questionService.findHotQuestions();
        model.addAttribute("pagination",pagination);
        model.addAttribute("questions",questions);
        return "index";
    }*/

}
