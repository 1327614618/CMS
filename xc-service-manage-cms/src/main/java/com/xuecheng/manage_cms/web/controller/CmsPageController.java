package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
//        //暂时采用测试数据，测试接口是否可以正常运行
//        QueryResult queryResult = new QueryResult();
//        queryResult.setTotal(2);
//        //静态数据列表
//        List arrayList = new ArrayList();
//        CmsPage cmsPage = new CmsPage();
//        cmsPage.setPageName("测试");
//        arrayList.add(cmsPage);
//        queryResult.setList(arrayList);
//        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
//        return queryResponseResult;
        return pageService.findList(page, size, queryPageRequest);
    }

    @Override
    @PostMapping("/add")
    public CmsPageResult addPageList(@RequestBody CmsPage cmsPage) {
        return pageService.addPageList(cmsPage);
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable String id) {
        return pageService.delete(id);
    }


    @Override
    @GetMapping("/findCmsTemplate")
    public QueryResponseResult findCmsTemplate() {
        return pageService.findCmsTemplate();
    }

    @Override
    @GetMapping("/findCmsSite")
    public QueryResponseResult findCmsSite() {
        return pageService.findCmsSite();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    @GetMapping("/findById/{id}")
    public CmsPageResult findById(@PathVariable String id) {
        return pageService.findById(id);
    }

    /**
     * 更新用户
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("/edit/{id}")    //这里使用put方法，http 方法中put表示更新
    public CmsPageResult edit(@PathVariable String id,@RequestBody CmsPage cmsPage) {
        return pageService.update(id, cmsPage);
    }

}
