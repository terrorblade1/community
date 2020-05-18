package com.java.community.controller;

import com.java.community.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: yk
 * Date: 2020/5/18 10:51
 */
@Controller
public class FileController {

    @RequestMapping("/file/upload")
    public @ResponseBody FileDTO upload(){
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/default-avatar.png");

        return fileDTO;
    }

}
