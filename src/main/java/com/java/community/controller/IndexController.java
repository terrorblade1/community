package com.java.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Author: yk
 * Date: 2020/5/5 18:24
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String hello(){
        return "index";
    }

}
