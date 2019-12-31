package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="cms课程计划查询接口",description = "课程计划查询")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachPlanList(String courseId);
}
