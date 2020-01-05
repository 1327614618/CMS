package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {
    @Autowired
    private CmsPageClient cmsPageClient;
    @Test
    public void testFeign() {
//通过服务id调用cms的查询页面接口
        CmsPage cmsPage = cmsPageClient.findById("5b3469f794db44269cb2bff1");
        System.out.println(cmsPage);
        while(true){}
    }
}