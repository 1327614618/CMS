package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.Query;
import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindPage() {
        int page = 0;
        //每页页数
        int size = 10;
        HashSet hashSet = new HashSet();
        PageRequest request = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(request);
        for (CmsPage cmsPage : all) {

            String pageAliase = cmsPage.getPageAliase();
            hashSet.add(pageAliase);
            System.out.println(hashSet);
        }
        System.out.println();
        System.out.println("---------------");
        System.out.println(LocalDateTime.now());
        System.out.println(all);

    }

    @Test
    public void testInsert() {
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage.getPageId());
        System.out.println(cmsPage);
    }

    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("5e006707d43fa8439c9c83d5");
    }

    //修改
    @Test
    public void testUpdate() {
//        Optional<CmsPage> optional = cmsPageRepository.findOne("5e006707d43fa8439c9c83d5");
//        if(optional.isPresent()){
//            CmsPage cmsPage = optional.get();
//            cmsPage.setPageName("测试页面01");
//            cmsPageRepository.save(cmsPage);
//        }
    }

    //自定义条件查询测试
    @Test
    public void testFindAll() {
//条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
//页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
//ExampleMatcher.GenericPropertyMatchers.contains() 包含
//ExampleMatcher.GenericPropertyMatchers.startsWith()//开头匹配
//条件值
        CmsPage cmsPage = new CmsPage();
//站点ID
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea2");
//模板ID
        cmsPage.setTemplateId("5a962c16b00ffc514038fafd");
// cmsPage.setPageAliase("分类导航");
//创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Pageable pageable = new PageRequest(0, 10);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        System.out.println(all);
    }

}
