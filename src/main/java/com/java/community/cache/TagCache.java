package com.java.community.cache;

import com.java.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: yk
 * Date: 2020/5/17 10:09
 */
public class TagCache {

    /**
     * 获取发布问题时供选择的标签信息
     * @return
     */
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<TagDTO>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("Java","Python","Perl","C","C++","C#",".NET","PHP","Swift","SQL","GO","MATLAB","Ruby","HTML","CSS","JavaScript","jQuery","GoLang","汇编语言"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("Spring","SpringMVC","MyBatis","SpringBoot","SpringCloud","Hibernate","Struts"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("Tomcat","NGINX","Resin","WebSphere","WebLogic"));
        tagDTOS.add(server);

        TagDTO database = new TagDTO();
        database.setCategoryName("数据库");
        database.setTags(Arrays.asList("MySQL","oracle","SqlServer","Redis","MongoDB"));
        tagDTOS.add(database);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("IntelliJ IDEA","eclipse","HBuilder","Maven","git","visual-studio"));
        tagDTOS.add(tool);

        TagDTO others = new TagDTO();
        others.setCategoryName("其他");
        others.setTags(Arrays.asList("提问","聊天","灌水"));
        tagDTOS.add(others);

        return tagDTOS;
    }

    public static String filterInvalid(String tags){
        String[] split = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOS = get();
        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }

}
