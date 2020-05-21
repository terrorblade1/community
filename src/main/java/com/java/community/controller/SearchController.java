package com.java.community.controller;

import com.java.community.dto.PaginationDTO;
import com.java.community.dto.QuestionDTO;
import com.java.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Author: yk
 * Date: 2020/5/21 10:43
 */
@Controller
public class SearchController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/search")
    public String search(String keyword,
                         @RequestParam(name = "page", defaultValue = "1") Integer page,
                         @RequestParam(name = "size", defaultValue = "6") Integer size,
                         Model model){
        if ("".equals(keyword) || keyword == null){
            return "redirect:/";
        }
        PaginationDTO pagination = null;
        try {
            pagination = questionService.findByElasticSearch(keyword, page, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<QuestionDTO> questions = questionService.findHotQuestions();
        model.addAttribute("pagination",pagination);
        model.addAttribute("questions",questions);
        return "index";
    }

}
