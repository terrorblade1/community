package com.java.community.service.impl;

import com.java.community.dto.PaginationDTO;
import com.java.community.dto.QuestionDTO;
import com.java.community.mapper.QuestionMapper;
import com.java.community.mapper.UserMapper;
import com.java.community.model.Question;
import com.java.community.model.User;
import com.java.community.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/7 9:17
 */
@Service
@Transactional(readOnly = false)
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 保存问题
     * @param title
     * @param description
     * @param tag
     */
    @Override
    public void save(String title, String description, String tag, Integer creator) {
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(creator);
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.insert(question);
    }

    /**
     * 分页查询全部问题
     * @return
     * @param page
     * @param size
     */
    @Override
    public PaginationDTO findAll(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.selectCount();  //总条数
        Integer totalPage;  //总页数
        if (totalCount % size == 0){
            //总页数 = 总条数 / 每页条数
            totalPage = totalCount / size;
        } else {
            //总页数 = 总条数 / 每页条数 + 1
            totalPage = totalCount / size +1;
        }
        if (page < 1){
            page = 1;  //页码小于1则设置成1
        }
        if (page > totalPage){
            page = totalPage;  //页码大总页码则设置成总页码
        }
        paginationDTO.setPagination(totalPage,page);  //设置传到页面的分页数据
        //size * (page - 1)
        Integer offset = size * (page - 1);
        //select * from question limit #{offset},#{size}
        List<Question> questions = questionMapper.selectAll(offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question:questions){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //这是Spring框架内置的一个工具类
            //此方法作用为: 把 question 中的属性赋值给 questionDTO
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    /**
     * 根据用户id分页查询该用户发布的问题
     * @param id
     * @param page
     * @param size
     * @return
     */
    @Override
    public PaginationDTO findById(Integer id, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.selectCountById(id);  //总条数
        Integer totalPage;  //总页数
        if (totalCount % size == 0){
            //总页数 = 总条数 / 每页条数
            totalPage = totalCount / size;
        } else {
            //总页数 = 总条数 / 每页条数 + 1
            totalPage = totalCount / size +1;
        }
        if (page < 1){
            page = 1;  //页码小于1则设置成1
        }
        if (page > totalPage){
            page = totalPage;  //页码大总页码则设置成总页码
        }
        paginationDTO.setPagination(totalPage,page);  //设置传到页面的分页数据
        //size * (page - 1)
        Integer offset = size * (page - 1);
        //select * from question limit #{offset},#{size}
        List<Question> questions = questionMapper.selectById(id,offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question:questions){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //这是Spring框架内置的一个工具类
            //此方法作用为: 把 question 中的属性赋值给 questionDTO
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }
}
