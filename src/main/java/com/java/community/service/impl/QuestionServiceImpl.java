package com.java.community.service.impl;

import com.java.community.dto.PaginationDTO;
import com.java.community.dto.QuestionDTO;
import com.java.community.exception.CustomizeErrorCode;
import com.java.community.exception.CustomizeException;
import com.java.community.mapper.QuestionExtMapper;
import com.java.community.mapper.QuestionMapper;
import com.java.community.mapper.UserMapper;
import com.java.community.model.Question;
import com.java.community.model.QuestionExample;
import com.java.community.model.User;
import com.java.community.service.QuestionService;
import org.apache.ibatis.session.RowBounds;
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
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    /**
     * 分页查询全部问题
     * @return
     * @param page
     * @param size
     */
    @Override
    public PaginationDTO findAll(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());  //总条数
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
        //分页查询
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question:questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
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
    public PaginationDTO findByUserId(Integer id, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;  //总页数
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(id);
        Integer totalCount = (int) questionMapper.countByExample(example);  //总条数
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
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(id);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question:questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
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
     * 根据id查找问题
     * @param id
     * @return
     */
    @Override
    public QuestionDTO findById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUNT);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    @Override
    public void saveOrModify(Question question) {
        if (question.getId() == null){  //添加
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertSelective(question);
        } else {  //更新
            question.setGmtModified(System.currentTimeMillis());
            int upd = questionMapper.updateByPrimaryKeySelective(question);
            if (upd != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUNT);
            }
        }
    }

    /**
     * 累加阅读数
     * @param id
     */
    @Override
    public void incView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}
