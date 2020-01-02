package com.xuecheng.manage_cms.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "xc-service-manage-cms")
public interface CmsPageClient {
    @GetMapping("/cms/page/findById/{id}")
    public CmsPage findById(@PathVariable("id") String id);
}