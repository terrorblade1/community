package com.java.community.service.impl;

import com.java.community.dto.CommentDTO;
import com.java.community.enums.CommentTypeEnum;
import com.java.community.exception.CustomizeErrorCode;
import com.java.community.exception.CustomizeException;
import com.java.community.mapper.CommentMapper;
import com.java.community.mapper.QuestionExtMapper;
import com.java.community.mapper.QuestionMapper;
import com.java.community.model.Comment;
import com.java.community.model.CommentExample;
import com.java.community.model.Question;
import com.java.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/9 20:28
 */
@Service
@Transactional(readOnly = false)
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Override
    public void save(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }

    @Override
    public List<CommentDTO> listByQuestionId(Long id) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id);
        commentMapper.selectByExample(example);
        return null;
    }
}
