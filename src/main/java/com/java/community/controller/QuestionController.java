package com.java.community.controller;

import com.java.community.dto.QuestionDTO;
import com.java.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Author: yk
 * Date: 2020/5/8 18:25
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                            Model model){
        QuestionDTO questionDTO = questionService.findById(id);
        model.addAttribute("question",questionDTO);
        return "question";
    }

}
