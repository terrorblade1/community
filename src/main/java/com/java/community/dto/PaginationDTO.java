package com.java.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/7 17:34
 */
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;//页面要显示的话题数据
    private boolean showPrevious;//向前按钮 <
    private boolean showFirstPage;//第一页按钮 <<
    private boolean showNext;//向后按钮 >
    private boolean showEndPage;//最后页按钮 >>
    private Integer page;//当前页面
    private List<Integer> pages = new ArrayList<>();//页面显示的页码,其中page是当前页
    private Integer totalPage;//总页数

    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage = totalPage;
        this.page = page;

        //显示页码的逻辑
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);
            }
            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        //是否展示上一页
        if (page == 1){
            showPrevious = false;
        } else {
            showPrevious = true;
        }
        //是否展示下一页
        if (page == totalPage){
            showNext = false;
        } else {
            showNext = true;
        }
        //是否显示第一页
        if (pages.contains(1)){
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }
        //是否显示最后一页
        if (pages.contains(totalPage)){
            showEndPage = false;
        } else {
            showEndPage = true;
        }

    }
}
