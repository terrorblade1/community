package com.java.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private RestHighLevelClient restHighLevelClient;

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
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
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
        paginationDTO.setData(questionDTOList);
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
    public PaginationDTO findByUserId(Long id, Integer page, Integer size) {
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
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question:questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    /**
     * 根据id查找问题
     * @param id
     * @return
     */
    @Override
    public QuestionDTO findById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
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
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.insertSelective(question);
        } else {  //更新
            question.setGmtModified(System.currentTimeMillis());
            int upd = questionMapper.updateByPrimaryKeySelective(question);
            if (upd != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /**
     * 累加阅读数
     * @param id
     */
    @Override
    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    /**
     * 利用正则表达式查询和当前话题tag相关的话题
     * @param questionDTO
     * @return
     */
    @Override
    public List<QuestionDTO> findRelated(QuestionDTO questionDTO) {
        if (StringUtils.isBlank(questionDTO.getTag())){
            return new ArrayList<>();
        }
        String regexpTag = questionDTO.getTag().replaceAll("，|,", "|");
        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO quesDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,quesDTO);
            return quesDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }

    /**
     * 查询热门话题(浏览数前五)
     * @return
     */
    @Override
    public List<QuestionDTO> findHotQuestions(){
        List<Question> questions = questionExtMapper.selectHotQuestions();
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOS.add(questionDTO);
        }
        return questionDTOS;
    }

    /**
     * 根据单个tag查找相关话题
     * @param tag
     * @return
     */
    @Override
    public PaginationDTO findByTag(String tag, Integer page, Integer size){
        PaginationDTO paginationDTO = new PaginationDTO();
        List<Question> questions = questionExtMapper.selectByTag(tag);
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setData(questionDTOS);

        Integer totalPage;  //总页数
        Integer totalCount = questionDTOS.size();  //总条数
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
        return paginationDTO;
    }


    /**
     * 定时将话题数据存入elasticsearch中
     */
    @Override
    @Scheduled(cron = "0/60 * * * * ? ")//每60s执行一次
    public void saveDataToElasticSearch(){
        //判断索引是否存在
        GetIndexRequest request = new GetIndexRequest("community");
        boolean exists = false;
        try {
            exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exists){
            //删除索引再创建(清空之前的数据)
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("community");
            AcknowledgedResponse delete = null;
            try {
                delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
                CreateIndexRequest createIndexRequest = new CreateIndexRequest("community");
                CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //添加
        List<Question> questions = questionMapper.selectByExample(new QuestionExample());
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("60s");
        for (int i = 0; i < questionDTOList.size(); i++) {
            bulkRequest.add(new IndexRequest("community")
                    .source(JSON.toJSONString(questionDTOList.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过elasticsearch查找
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @Override
    public PaginationDTO findByElasticSearch(String keyword, Integer page, Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("community");
        //构建查询的条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 匹配所有 matchAllQueryBuilder / 精确匹配 termQueryBuilder
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keyword);
        sourceBuilder.query(matchQueryBuilder);
        //sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");//前缀
        highlightBuilder.postTags("</span>");//后缀
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();//获取高亮字段
            HighlightField title = highlightFields.get("title");
            Map<String, Object> map = hit.getSourceAsMap();
            if (title != null){//解析高亮字段 将原来的字段替换为高亮字段
                Text[] fragments = title.fragments();//取出字段
                String newTitle = "";
                for (Text text : fragments) {
                    newTitle += text;
                }
                map.put("title",newTitle);//替换
            }
            //封装
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(Long.parseLong(map.get("id").toString()));
            questionDTO.setTitle((String) map.get("title"));

            ObjectMapper objectMapper = new ObjectMapper();
            String userStr = objectMapper.writeValueAsString(map.get("user"));
            User user = objectMapper.readValue(userStr, User.class);
            questionDTO.setUser(user);

            questionDTO.setDescription((String) map.get("description"));
            questionDTO.setTag((String) map.get("tag"));
            questionDTO.setGmtCreate(Long.parseLong(map.get("gmtCreate").toString()));
            questionDTO.setGmtModified(Long.parseLong(map.get("gmtModified").toString()));
            questionDTO.setCreator(Long.parseLong(map.get("creator").toString()));
            questionDTO.setCommentCount(Integer.parseInt(map.get("commentCount").toString()));
            questionDTO.setViewCount(Integer.parseInt(map.get("viewCount").toString()));
            questionDTO.setLikeCount(Integer.parseInt(map.get("likeCount").toString()));
            questionDTOList.add(questionDTO);
        }

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionDTOList.size();  //查询出的数据总条数
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
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

}
