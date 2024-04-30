package com.gra.backend.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.gra.backend.common.result.Result;
import com.gra.backend.pojo.Course;
import com.gra.backend.pojo.StudentCourse;
import com.gra.backend.pojo.User;
import com.gra.backend.service.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user/course")
public class CourseController {

    @Resource
    private CourseService courseService;


    @GetMapping("getCourseList")
    public Result<?> getAppointmentsOfDoctor() {
        return courseService.getCourseList();
    }

    @GetMapping("getCourseSchedule")
    public Result<?> getCourseSchedule() {
        return courseService.getCourseSchedule();
    }

    @GetMapping("getCourseAllList")
    public Result<?> getCourseAllList() {
        return courseService.getCourseAllList();
    }

    @PostMapping("addCourse")
    public Result<?> addCourse(Course course) {
        return courseService.addCourse(course);
    }

    @PostMapping("updateCourse")
    public Result<?> updateCourse(Course course) {
        return courseService.updateCourse(course);
    }

    //学生选课或退课
    @PostMapping("updateStudentCourse")
    public Result<?> updateStudentCourse(Course course) {
        return courseService.updateStudentCourse( course);
    }

    @PostMapping("getStudentsByCourseId")
    public Result<?> getStudentsByCourseId(Course course) {
        return courseService.getStudentsByCourseId(course);
    }

    @PostMapping("editScore")
    public Result<?> editScore(StudentCourse course) {
        return courseService.editScore(course);
    }

    @GetMapping("getExcel")
    public void exportExcel(HttpServletResponse response) {
        courseService.exportExcel(response);
    }

    @PostMapping("updateCourseHomeWork")
    public Result<?> updateCourseHomeWork(Course course){
       return   courseService.updateCourseHomeWork(course);
    }

}
