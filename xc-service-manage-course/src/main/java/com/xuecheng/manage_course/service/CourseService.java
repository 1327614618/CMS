package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachPlanRepository teachPlanRepository;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CoursePicRepository coursePicRepository;
    @Autowired
    private CourseMarketRepository courseMarketRepository;
    @Autowired
    private CmsPageClient cmsPageClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    public TeachplanNode findTeachPlanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }

    //添加课程计划
    public ResponseResult addTeachPaln(Teachplan teachplan) {
        //校验课程id和课程计划名称是否存在
        //这两个是必须传的参数getCourseid   getPname
        if (teachplan == null || StringUtils.isEmpty(teachplan.getPname()) || StringUtils.isEmpty(teachplan.getCourseid())) {
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        }
        //取出课程id
        String courseid = teachplan.getCourseid();
        //取出父结点id
        String parentid = teachplan.getParentid();
        //如果父结点为空则获取根结点
        if (StringUtils.isEmpty(parentid)) {
            //获取课程根结点，如果没有则添加根结点
            parentid = getTeachPlanRoot(courseid);
        }

        //取出父结点信息
        Optional<Teachplan> teachplanOptional = teachPlanRepository.findById(parentid);
        if (!teachplanOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        }
        //父结点
        Teachplan teachplanParent = teachplanOptional.get();
        //父结点级别
        String parentGrade = teachplanParent.getGrade();
        //设置父结点
        teachplan.setParentid(parentid);
        //设置是否发布
        teachplan.setStatus("0");
        //子结点的级别，根据父结点来判断
        if (parentGrade.equals("1")) {
            teachplan.setGrade("2");
        } else if (parentGrade.equals("2")) {
            teachplan.setGrade("3");
        }
        //设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachPlanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);

    }

    //获取课程根结点，如果没有则添加根结点
    public String getTeachPlanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        //校验课程id不存在数据的时候
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        //取出课程计划根结点
        //parentId根节点的都是0
        List<Teachplan> teachplanList = teachPlanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() == 0) {
            //新增一个根结点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            //1级
            teachplanRoot.setGrade("1");
            //未发布
            teachplanRoot.setStatus("0");

            teachPlanRepository.save(teachplanRoot);
            return teachplanRoot.getId();

        }
        Teachplan teachplan = teachplanList.get(0);
        return teachplan.getId();

    }

    public QueryResult<CourseInfo> findCourseBase() {
        //设置教育机构
        CourseListRequest courseListRequest = new CourseListRequest();
        courseListRequest.setCompanyId("1");
        //查询第一页，每页显示10条记录
        PageHelper.startPage(1, 10);

        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);

        QueryResult<CourseInfo> result = new QueryResult<>();
        result.setList(courseListPage);
        return result;
    }

    /**
     * //查询课程图片
     *
     * @param courseId
     * @param pic
     * @return
     */
    public ResponseResult saveCoursePic(String courseId, String pic) {
        //查询课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (optional.isPresent()) {
            coursePic = optional.get();
        }
        //没有课程图片则新建对象
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        //是否为空
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);

        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);

    }

    //根据id查询图片
    public CoursePic findCoursepic(String courseId) {
        CoursePic coursePic = coursePicRepository.findById(courseId).orElse(null);

        Optional<CoursePic> one = coursePicRepository.findById(courseId);
        return one.get();

    }

    //删除课程图片
    public ResponseResult deleteCoursePic(String courseId) {

        coursePicRepository.deleteById(courseId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //课程视图查询
    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        //查询课程基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);

        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程营销信息
        Optional<CourseMarket> courseMarket = courseMarketRepository.findById(id);
        if (courseMarket.isPresent()) {
            CourseMarket market = courseMarket.get();
            courseView.setCourseMarket(market);
        }

        //查询课程图片信息
        Optional<CoursePic> coursePic = coursePicRepository.findById(id);
        if (coursePic.isPresent()) {
            CoursePic pic = coursePic.get();
            courseView.setCoursePic(pic);
        }
        //查询课程计划信息

        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    /**
     * 根据id查询课程基本信息
     *
     * @param courseId
     * @return
     */
    public CourseBase findCurseBaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    /**
     * 课程发布页面预览
     *
     * @param courseId
     * @return
     */
    public CoursePublishResult preview(String courseId) {
        CourseBase one = this.findCurseBaseById(courseId);
        //发布课程页面预览
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面别名
        cmsPage.setPageName(courseId + ".html");

        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //远程请求
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        //页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //页面url
        String pageUrl = previewUrl + pageId;
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);

    }

    @Transactional
    public CoursePublishResult publish(String courseId) {

        CourseBase one = this.findCurseBaseById(courseId);
        CmsPostPageResult cmsPostPageResult = this.publishPage(courseId);
        if (!cmsPostPageResult.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //更新课程状态
        CourseBase courseBase = saveCoursePubState(courseId);
        //课程索引...
        //课程缓存...
        //页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
        
    }

    //更新课程发布状态
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBase = this.findCurseBaseById(courseId);
        //更新发布状态
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }

    /**
     * 发布课程正式页面
     *
     * @param courseId
     * @return
     */
    public CmsPostPageResult publishPage(String courseId) {
        CourseBase one = this.findCurseBaseById(courseId);
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        //课程预览站点
        cmsPage.setSiteId(publish_siteId);
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //发布页面
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        return cmsPostPageResult;
    }

}

