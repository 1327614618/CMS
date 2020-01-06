package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;



    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanList(@PathVariable("courseId") String courseId) {
        TeachplanNode teachPlanList = courseService.findTeachPlanList(courseId);
        return teachPlanList;
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachPlanList(@RequestBody Teachplan teachplan) {
        return courseService.addTeachPaln(teachplan);
    }

    @Override
    public TeachplanNode deleteTeachPlanById(String courseId) {
        return null;
    }

    @Override
    @GetMapping("/coursebase/list")
    public QueryResult<CourseInfo> findCourseBase() {
        return courseService.findCourseBase();
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseService.saveCoursePic(courseId,pic);
    }
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursepic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id) {
        return courseService.getCoruseView(id);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

}
