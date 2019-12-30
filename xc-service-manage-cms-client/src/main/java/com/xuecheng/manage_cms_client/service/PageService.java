package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;

    //保存页面到服务器路径
    public void savePageToServerPath(String pageId) {
        //取出页面物理路径
        CmsPage cmsPage = this.getCmsPage(pageId);
        //页面所属站点
        CmsSite cmsSite = this.getCmsSite(cmsPage.getSiteId());
        //页面物理路径
        String pagePath = cmsSite.getSitePhysicalPath() + cmsPage.getPagePhysicalPath() +
                cmsPage.getPageName();
        String htmlFileId = cmsPage.getHtmlFileId();
        //查询页面静态文件
        InputStream inputStream = this.getFileId(htmlFileId);
        if(inputStream == null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            //将文件内容保存到服务物理路径
            try {
                IOUtils.copy(inputStream, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    // 获取页面静态化流
    private InputStream getFileId(String pageHtmlFileId){
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(pageHtmlFileId)));

        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource resource = new GridFsResource(gridFSDownloadStream.getGridFSFile());
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
          return null;
    }

     //根据id获取网址内容
    private CmsSite getCmsSite(String pageCmsSiteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(pageCmsSiteId);

        if (optional.isPresent()) {
            CmsSite cmsSite = optional.get();
            return cmsSite;
        } else {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        return null;
    }

    //根据id获取网址内容
    private CmsPage getCmsPage(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);

        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            return cmsPage;
        } else {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        return null;
    }
}
