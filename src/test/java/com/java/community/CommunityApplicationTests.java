package com.java.community;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.community.dto.PaginationDTO;
import com.java.community.dto.QuestionDTO;
import com.java.community.mapper.QuestionMapper;
import com.java.community.mapper.UserMapper;
import com.java.community.model.Question;
import com.java.community.model.QuestionExample;
import com.java.community.model.User;
import com.java.community.service.QuestionService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CommunityApplicationTests {

	@Autowired
	private QuestionMapper questionMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private QuestionService questionService;

	@Autowired
	@Qualifier("restHighLevelClient")
	private RestHighLevelClient client;

	//创建索引
	@Test
	public void testCreateIndex() throws IOException {
		//1. 创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("community");
		//2. 客户端执行请求 IndicesClient 请求后获得相应
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(createIndexResponse);
	}

	//判断索引是否存在
	@Test
	public void testExistIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("test1");
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//删除索引
	@Test
	public void testDeleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("test1");
		AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println(delete.isAcknowledged());
	}

	//测试添加文档  put /index/_doc/id
	@Test
	public void testAddDocument() throws IOException {
		//创建对象
		User user = new User();
		user.setName("川建国");
		user.setId(1L);
		user.setAvatarUrl("xxxxxxxxxxxxxx");
		//创建请求
		IndexRequest request = new IndexRequest("community");
		request.id("1");
		request.timeout("1s");
		//     将数据放入请求 !
		request.source(JSON.toJSONString(user), XContentType.JSON);
		//客户端发送请求 获取响应结果
		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
		System.out.println(indexResponse.status());//对应命令返回的状态 CREATED
		System.out.println(indexResponse.toString());//
	}

	//判断文档是否存在 get /index/_doc/1
	@Test
	public void testIsExists() throws IOException {
		GetRequest getRequest = new GetRequest("test1","1");
		//不获取返回的 _source 的上下文
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//获取文档信息
	@Test
	public void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("test1","1");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		System.out.println(getResponse.getSourceAsString());//打印文档内容
		System.out.println(getResponse);
	}


	//更新文档信息
	@Test
	public void testUpdateDocument() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("test1","1");
		updateRequest.timeout("1s");
		//创建对象
		User user = new User();
		user.setName("宇宙网红川建国");
		user.setId(1L);
		user.setAvatarUrl("ghs");
		updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
		UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
		System.out.println(update.status());
		System.out.println(update);
	}

	//删除文档记录
	@Test
	public void testDeleteDocument() throws IOException {
		DeleteRequest request = new DeleteRequest("test1", "1");
		request.timeout("1s");
		DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
		System.out.println(deleteResponse.status());
	}

	//批量添加
	@Test
	public void testBulkRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("10s");
		List<QuestionDTO> questions = questionService.findHotQuestions();

		for (int i = 0; i < questions.size(); i++) {
			//批量更新删除在这里修改对应的请求就行
			bulkRequest.add(
			        new IndexRequest("test1")
				    //.id(""+(i+1))  //id不写也行,会生成随机的
				    .source(JSON.toJSONString(questions.get(i)),XContentType.JSON));
		}

		BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulkItemResponses.hasFailures());
	}


	/**
	 * 查询
	 * SearchRequest  搜索请求
	 * SearchSourceBuilder  条件构造
	 * HighlightBuilder  构建高亮
	 * TermQueryBuilder  构建精确查询
	 * matchAllQueryBuilder  构建查询所有
	 * ......
	 * @throws IOException
	 */
	@Test
	public void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest("test1");
		//构建搜索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();//匹配所有
		//TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "SpringBoot");//精确匹配

		sourceBuilder.query(matchAllQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		//System.out.println(JSON.toJSONString(searchResponse.getHits()));
		for (SearchHit documentFields : searchResponse.getHits().getHits()) {
			System.out.println(documentFields.getSourceAsMap());
			System.out.println("--------------------------------------------------");
		}
	}

	/**
	 * 将全部话题数据装入索引中
	 * @throws IOException
	 */
	@Test
	public void testAddQuestions() throws IOException{
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
			bulkRequest.add(new IndexRequest("test1")
					.source(JSON.toJSONString(questionDTOList.get(i)),XContentType.JSON));
		}

		BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulkResponse.hasFailures());
	}


	/**
	 * 精确匹配 高亮显示
	 * @throws IOException
	 */
	@Test
	public void searchQuestions() throws IOException {
		SearchRequest searchRequest = new SearchRequest("test1");
		//构建查询的条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		//精准匹配
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "springboot");
		sourceBuilder.query(termQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
		//高亮显示
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("title");
		highlightBuilder.preTags("<span style='color:red'>");//前缀
		highlightBuilder.postTags("</span>");//后缀
		sourceBuilder.highlighter(highlightBuilder);
		//执行搜索
		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		//解析结果
		List<QuestionDTO> questionDTOList = new ArrayList<>();
		for (SearchHit documentFields : searchResponse.getHits().getHits()) {
			Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();//获取高亮字段
			HighlightField title = highlightFields.get("title");
			Map<String, Object> map = documentFields.getSourceAsMap();
			if (title != null){//解析高亮字段 将原来的字段替换为高亮字段
				Text[] fragments = title.fragments();//取出字段
				String newTitle = "";
				for (Text text : fragments) {
					newTitle += text;
				}
				map.put("title",newTitle);//替换
			}

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
		System.out.println(questionDTOList);
		System.out.println(questionDTOList.size());
	}


	//删除索引下的全部文档...
	@Test
	public void deleteAll() throws IOException {
		//删除索引再创建(清空之前的数据)
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("community");
		AcknowledgedResponse delete = null;
		try {
			delete = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
			CreateIndexRequest createIndexRequest = new CreateIndexRequest("community");
			CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
