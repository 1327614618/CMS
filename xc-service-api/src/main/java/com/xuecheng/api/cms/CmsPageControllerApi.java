package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.*;

@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    @ApiOperation("分页查询结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    //新增页面功能
    @ApiOperation("新增页面功能")
    public CmsPageResult addPageList(CmsPage cmsPage);

    @ApiOperation("删除")
    public ResponseResult delete(String id);

    @ApiOperation("查询模板")
    QueryResponseResult findCmsTemplate();

    @ApiOperation("查询站点")
    QueryResponseResult findCmsSite();

    @ApiOperation("通过ID查询页面")
    public CmsPageResult findById(String id);

    @ApiOperation("更新修改页面")
    public CmsPageResult edit(String id, CmsPage cmsPage);

    @ApiOperation("发布页面")
    public ResponseResult post(String pageId);

    @ApiOperation("保存页面")
    public CmsPageResult save(CmsPage cmsPage);


}
