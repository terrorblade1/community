package com.java.community.controller;

import com.java.community.dto.NotificationDTO;
import com.java.community.dto.PaginationDTO;
import com.java.community.enums.NotificationTypeEnum;
import com.java.community.model.User;
import com.java.community.service.NotificationService;
import com.java.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: yk
 * Date: 2020/5/8 10:55
 */
@Controller
public class NotificationController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,
                          HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id,user);

        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                 || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()){

            return "redirect:/question/"+notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }

    }

}
