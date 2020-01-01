package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
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
}
