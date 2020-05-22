package com.java.community.controller;

import com.java.community.dto.PaginationDTO;
import com.java.community.dto.QuestionDTO;
import com.java.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/22 16:10
 */
@Controller
public class TagController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/tag/{tag}")
    public String getBytag(@PathVariable(name = "tag") String tag,
                           @RequestParam(name = "page", defaultValue = "1") Integer page,
                           @RequestParam(name = "size", defaultValue = "20") Integer size,
                           Model model){
        PaginationDTO pagination = questionService.findByTag(tag, page, size);
        List<QuestionDTO> questions = questionService.findHotQuestions();
        model.addAttribute("pagination",pagination);
        model.addAttribute("questions",questions);
        return "index";
    }

}
