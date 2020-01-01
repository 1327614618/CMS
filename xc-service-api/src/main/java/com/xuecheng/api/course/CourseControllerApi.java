package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value="cms课程计划查询接口",description = "课程计划查询")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachPlanList(String courseId);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachPlanList(Teachplan teachplan);

    @ApiOperation("删除")
    TeachplanNode deleteTeachPlanById(String courseId);

    @ApiOperation("查询我的课程")
    QueryResult<CourseInfo> findCourseBase();

}
