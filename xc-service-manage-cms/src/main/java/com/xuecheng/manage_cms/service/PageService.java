package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        //定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //定义条件对象
        CmsPage cmsPage = new CmsPage();
        //页面ID
        if (StringUtils.isNoneEmpty(queryPageRequest.getPageId())) {
            cmsPage.setPageId(queryPageRequest.getPageId());
        }
        //站点id
        if (StringUtils.isNoneEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //模版id
        if (StringUtils.isNoneEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //别名
        if (StringUtils.isNoneEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //页面名称
        if (StringUtils.isNoneEmpty(queryPageRequest.getPageName())) {
            cmsPage.setPageName(queryPageRequest.getPageName());
        }

        //创建
        Example<CmsPage> cmsPageExample = Example.of(cmsPage, exampleMatcher);
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        //为了适应mongodb的接口将页码减1
        if (size <= 0) {
            size = 20;
        }
        //分页对象
        PageRequest pageRequest = new PageRequest(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(cmsPageExample, pageRequest);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setTotal(all.getTotalElements());
        cmsPageQueryResult.setList(all.getContent());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
        return queryResponseResult;
    }

    /**
     * 查询模板
     *
     * @return
     */
    public QueryResponseResult findCmsTemplate() {
        List<CmsTemplate> all = cmsTemplateRepository.findAll();
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    public QueryResponseResult findCmsSite() {
        //调用放方法
        List<CmsSite> all = cmsSiteRepository.findAll();
        //通用传输
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    public CmsPageResult addPageList(CmsPage cmsPage) {
        //校验cmsPage是否为空
        if (cmsPage == null) {
            return new CmsPageResult(CmsCode.CMS_COURSE_PERVIEWISNULL, cmsPage);
        }
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage cmsPageList = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(
                cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPageList != null) {
            //页面已存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //若不存在
        if (cmsPageList == null) {
            //添加页面主键由spring data 自动生成
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            //返回结果
            return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        }

        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 根据id查询页面
     *
     * @param id
     * @return
     */
    public CmsPageResult findById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            CmsPage page = optional.get();
            CmsPageResult result = new CmsPageResult(CommonCode.SUCCESS, page);
            return result;
        }
        return null;
    }

    /**
     * 通过id查找cms页面数据信息
     *
     * @param id
     * @return
     */
    private CmsPage findCmsPageById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            CmsPage page = optional.get();
            return page;
        }
        return null;
    }

    /**
     * 编辑更新页面
     * <p>
     * //修改
     *
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //查询页面
        Optional<CmsPage> cms1 = cmsPageRepository.findById(id);
        CmsPage cms = cms1.get();
        //CmsPage cms = this.findById(id);
        if (cms != null) {
            cms.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            cms.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            cms.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            cms.setPageName(cmsPage.getPageName());
            //更新访问路径
            cms.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            cms.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //设置rul
            cms.setDataUrl(cmsPage.getDataUrl());
            CmsPage save = cmsPageRepository.save(cms);
            //判断是否保存成功
            if (save != null) {
                return new CmsPageResult(CommonCode.SUCCESS, save);
            }
        }

        return new CmsPageResult(CommonCode.FAIL, cms);
    }

    public ResponseResult delete(String id) {
        //查询
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        //获取到byid转换数据类型
        CmsPage cmsPage = byId.get();
        if (cmsPage != null) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }

    //页面静态化
    public String PageHtml(String id) {
        //获取页面模型数据
        Map modelByPageId = this.getModelByPageId(id);
        if (modelByPageId == null) {
            //获取页面模型数据为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //获取页面模板
        String templateByPageId = this.getTemplateByPageId(id);
        if (templateByPageId == null) {
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        //执行静态化
        String generateHtml = this.generateHtml(templateByPageId, modelByPageId);
        return generateHtml;
    }

    //页面静态化
    public String generateHtml(String template, Map model) {
        //生成配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //加载模板器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", template);
        //配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);

        //获取模板
        try {
            Template temp = configuration.getTemplate("template");
            //免费的标记实用程序
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(temp, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取页面模板
    public String getTemplateByPageId(String pageId) {
        CmsPage cmsPage = this.findCmsPageById(pageId);
        if (cmsPage == null) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //页面模板
        String templateId = cmsPage.getTemplateId();
        if (templateId == null) {
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            //模板文件id
            CmsTemplate template = optional.get();
            String templateFileId = template.getTemplateFileId();
            //取出模板文件内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流对象
            GridFSDownloadStream stream;
            try {
                stream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
                //创建GridFsResource
                GridFsResource gridFsResource = new GridFsResource(gridFSFile, stream);
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    //获取页面模型数据
    public Map getModelByPageId(String id) {
        //查询页面信息
        CmsPage cmsPage = this.findCmsPageById(id);
        if (cmsPage == null) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出DateURL
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //通过DateUrl和Map.class获取到实体模板
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        //获取内容
        Map body = forEntity.getBody();
        return body;

    }

    //页面发布
    public ResponseResult postPage(String pageId) {
        //执行静态化
        String pageHtml = this.PageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //保存静态化文件

        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        sendPostPage(pageId);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //发送页面发布消息
    private void sendPostPage(String pageId) {
        CmsPage cmsPage = this.findCmsPageById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("pageId", pageId);
        //消息内容
        String msg = JSON.toJSONString(msgMap);
        //获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();
        //发布消息
        this.rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, msg);
    }

    /**
     * 保存静态页面内容
     *
     * @param pageId
     * @param content
     * @return
     */
    private CmsPage saveHtml(String pageId, String content) {

        //查询页面
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();
        //存储前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        //保存到gridfs
        InputStream inputStream = IOUtils.toInputStream(content);
        //文件id
        ObjectId store = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        String fileId = store.toString();
        //保存带cmspage中
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);

        return cmsPage;
    }

    /**
     * 保存课程页面
     *
     * @param cmsPage
     * @return
     */
    public CmsPageResult save(CmsPage cmsPage) {
        // CmsPage cms = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
        //        cmsPage.getPageType(), cmsPage.getPageWebPath());
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage cms = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cms != null) {
            //更新
            return this.update(cms.getPageId(), cmsPage);
        } else {
            //添加
            return this.addPageList(cmsPage);
        }

    }

    //一键发布页面
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //添加页面
        CmsPageResult save = this.save(cmsPage);
        if (!save.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        //要布的页面id
        CmsPage cmsPage1 = save.getCmsPage();

        //发布页面
        ResponseResult responseResult = this.postPage(cmsPage1.getPageId());
        if (!responseResult.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        //得到页面的url
        //页面url=站点域名+站点webpath+页面webpath+页面名称
        //站点id
        String siteId = cmsPage1.getSiteId();
        //查询站点信息
        CmsSite cmsSiteById = findCmsSiteById(siteId);
        //站点域名
        String siteDomain = cmsSiteById.getSiteDomain();
        //站点web路径
        String siteWebPath = cmsSiteById.getSiteWebPath();
        //页面web
        String pageWebPath = cmsPage1.getPageWebPath();
        //页面名称
        String pageName = cmsPage1.getPageName();
        //页面的web访问地址
        String pageUrl = siteDomain+siteWebPath+pageWebPath+pageName;
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }
    //根据id查询站点信息
    private CmsSite findCmsSiteById(String cmsSiteId){
        Optional<CmsSite> op = cmsSiteRepository.findById(cmsSiteId);
        if (op.isPresent()){

            CmsSite cmsSite = op.get();
            return   cmsSite;
        }
        return null;

        
    }
}
