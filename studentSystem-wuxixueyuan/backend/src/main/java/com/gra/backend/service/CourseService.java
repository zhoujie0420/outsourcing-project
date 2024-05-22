package com.gra.backend.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.enums.WeekEnum;
import com.gra.backend.common.result.Result;
import com.gra.backend.mapper.CourseMapper;
import com.gra.backend.mapper.StudentCourseMapper;
import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.Course;
import com.gra.backend.pojo.StudentCourse;
import com.gra.backend.pojo.User;
import com.gra.backend.utils.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseService {


    @Resource
    private CourseMapper courseMapper;
    @Resource
    private StudentCourseMapper studentCourseMapper;
    @Resource
    private UserMapper userMapper;

    public Result<?> getCourseList() {
        User user = UserUtil.getUser();
        List<Course> res;
        if (user.getRole() == 1) {
            LambdaQueryWrapper<StudentCourse> studentCourseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentCourseLambdaQueryWrapper.eq(StudentCourse::getStudentId, user.getId());
            List<StudentCourse> studentCourses = studentCourseMapper.selectList(studentCourseLambdaQueryWrapper);
            // 获取courseId
            List<Integer> collect = studentCourses.stream().map(StudentCourse::getCourseId).collect(Collectors.toList());
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.in(Course::getId, collect).eq(Course::getState, 1);
            res = courseMapper.selectList(courseLambdaQueryWrapper);
        } else {
            LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Course::getTeacherId, user.getId()).orderBy(true, false, Course::getState);
            res = courseMapper.selectList(wrapper);
        }
        for (Course re : res) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getId, re.getTeacherId());
            String username = userMapper.selectOne(userLambdaQueryWrapper).getUsername();
            re.setTeacher(username);
            Integer weekTime = re.getWeekTime();
            String nameByCode = WeekEnum.getNameByCode(weekTime);
            re.setTime(nameByCode + " " + "第" + re.getDayTime() + "节");
        }
        return Result.success(res);
    }

    public Result<?> getCourseSchedule() {
        User user = UserUtil.getUser();
        List<List<String>> res = new ArrayList<>();
        if (user.getRole() == 1) {
            LambdaQueryWrapper<StudentCourse> studentCourseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentCourseLambdaQueryWrapper.eq(StudentCourse::getStudentId, user.getId());
            List<StudentCourse> studentCourses = studentCourseMapper.selectList(studentCourseLambdaQueryWrapper);
            // 获取courseId
            List<Integer> collect = studentCourses.stream().map(StudentCourse::getCourseId).collect(Collectors.toList());
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.in(Course::getId, collect);
            List<Course> courses = new ArrayList<>();
            try {
                courses = courseMapper.selectList(courseLambdaQueryWrapper);
            } catch (Exception e) {
                courses = new ArrayList<>();
            }
            for (int i = 1; i <= 8; ++i) {
                List<String> temp = new ArrayList<>();
                temp.add("第" + i + "节");
                for (int j = 1; j <= 7; ++j) {
                    boolean flag = false;
                    for (Course course : courses) {
                        if (course.getWeekTime() == j && course.getDayTime() == i) {
                            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            userLambdaQueryWrapper.eq(User::getId, course.getTeacherId());
                            String username = userMapper.selectOne(userLambdaQueryWrapper).getUsername();
                            temp.add(course.getName() + "/" + username);
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        temp.add("");
                    }
                }
                res.add(temp);
            }
        } else {
            LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Course::getTeacherId, user.getId());
            List<Course> courses = courseMapper.selectList(wrapper);
            for (Course course : courses) {
                List<String> temp = new ArrayList<>();
                temp.add(course.getName());
                temp.add(course.getTeacher());
                temp.add(course.getTime());
                res.add(temp);
            }
        }
        return Result.success(res);
    }

    public Result<?> getCourseAllList() {
        User user = UserUtil.getUser();
        List<Course> res = courseMapper.selectList(null);
        for (Course re : res) {
            if (re.getState().equals(0)) {
                re.setState(0);
            } else {
                //判断是否可以选择
                LambdaQueryWrapper<StudentCourse> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
                courseLambdaQueryWrapper.eq(StudentCourse::getCourseId, re.getId()).eq(StudentCourse::getStudentId, user.getId());
                StudentCourse studentCourse = studentCourseMapper.selectOne(courseLambdaQueryWrapper);
                if (studentCourse != null) {
                    //已经选择 1
                    re.setState(1);
                } else {
                    //判断是否时间冲突
                    LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Course::getYearDate, re.getYearDate()).eq(Course::getWeekTime, re.getWeekTime()).eq(Course::getDayTime, re.getDayTime());
                    List<Integer> collect = courseMapper.selectList(queryWrapper).stream().map(Course::getId).collect(Collectors.toList());

                    LambdaQueryWrapper<StudentCourse> studentCourseLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    studentCourseLambdaQueryWrapper.eq(StudentCourse::getStudentId, user.getId()).in(StudentCourse::getCourseId, collect);

                    List<StudentCourse> studentCourses = studentCourseMapper.selectList(studentCourseLambdaQueryWrapper);
                    if (!studentCourses.isEmpty()) {
                        //时间冲突 2
                        re.setState(2);
                    } else {
                        // 3
                        re.setState(3);
                    }
                }
            }
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getId, re.getTeacherId());
            String username = userMapper.selectOne(userLambdaQueryWrapper).getUsername();
            re.setTeacher(username);
            Integer weekTime = re.getWeekTime();
            String nameByCode = WeekEnum.getNameByCode(weekTime);
            re.setTime(nameByCode + " " + "第" + re.getDayTime() + "节");
        }
        //根据state排序
        res.sort((o1, o2) -> {
            if (o1.getState().equals(o2.getState())) {
                return 0;
            }
            return o1.getState() > o2.getState() ? 1 : -1;
        });
        return Result.success(res);
    }

    public Result<?> addCourse(Course course) {
        User user = UserUtil.getUser();
        if (user.getRole() != 2) {
            return Result.fail("权限不足");
        }
        //判断是否已经存在
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getYearDate, course.getYearDate()).eq(Course::getWeekTime, course.getWeekTime()).eq(Course::getDayTime, course.getDayTime());
        List<Course> courses = courseMapper.selectList(queryWrapper);
        if (!courses.isEmpty()) {
            return Result.fail("该时间段已经存在课程");
        }
        course.setTeacherId(user.getId());
        course.setState(0);
        courseMapper.insert(course);
        return Result.success();
    }

    public Result<?> updateCourse(Course course) {
        Course newCourse = courseMapper.selectByIdDeep(course.getId());
        if (newCourse == null) {
            return Result.fail("课程不存在");
        }
        //遍历course的字段，不为空就更新，用反射
        if (course.getName() != null) {
            newCourse.setName(course.getName());
        }
        if (course.getScore() != null) {
            newCourse.setScore(course.getScore());
        }
        if (course.getYearDate() != null) {
            newCourse.setYearDate(course.getYearDate());
        }
        if (course.getWeekTime() != null) {
            newCourse.setWeekTime(course.getWeekTime());
        }
        if (course.getDayTime() != null) {
            newCourse.setDayTime(course.getDayTime());
        }
        if (course.getCourseInfoId() != null) {
            newCourse.setCourseInfoId(course.getCourseInfoId());
        }
        if (course.getState() != null) {
            newCourse.setState(course.getState());
        }
        courseMapper.updateById(newCourse);
        return Result.success();
    }

    public Result<?> updateStudentCourse(Course course) {
        User user = UserUtil.getUser();
        //选课
        if (course.getStudentCourseState() == 1) {
            studentCourseMapper.insert(new StudentCourse(null, user.getId(), course.getId(), "0"));
        } else {
            studentCourseMapper.delete(new LambdaQueryWrapper<StudentCourse>().eq(StudentCourse::getStudentId, user.getId()).eq(StudentCourse::getCourseId, course.getId()));
        }
        return Result.success();
    }

    public Result<?> getStudentsByCourseId(Course course) {
        List<StudentCourse> studentCourses = studentCourseMapper.selectList(new LambdaQueryWrapper<StudentCourse>().eq(StudentCourse::getCourseId, course.getId()));
        List<User> users = new ArrayList<>();
        for (StudentCourse studentCourse : studentCourses) {
            User user = userMapper.selectById(studentCourse.getStudentId());
            user.setScore(studentCourse.getScore());
            users.add(user);
        }
        return Result.success(users);
    }

    public Result<?> editScore(StudentCourse studentCourse) {
        LambdaQueryWrapper<StudentCourse> studentCourseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentCourseLambdaQueryWrapper.eq(StudentCourse::getCourseId, studentCourse.getCourseId()).eq(StudentCourse::getStudentId, studentCourse.getStudentId());
        StudentCourse newCourse = studentCourseMapper.selectOne(studentCourseLambdaQueryWrapper);
        newCourse.setScore(studentCourse.getScore());
        studentCourseMapper.updateById(newCourse);
        return Result.success();
    }

    public void exportExcel(HttpServletResponse response) {
        List<StudentCourse> studentCourses = studentCourseMapper.selectList(null);
        List<User> users = new ArrayList<>();
        for (StudentCourse studentCourse : studentCourses) {
            User user = userMapper.selectById(studentCourse.getStudentId());
            user.setScore(studentCourse.getScore());
            users.add(user);
        }
        try {
            this.setExcelResponseProp(response, "用户列表");
            EasyExcel.write(response.getOutputStream()).head(User.class).excelType(ExcelTypeEnum.XLSX).sheet("用户列表").doWrite(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setExcelResponseProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException, UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }


    public Result<?> updateCourseHomeWork(Course course) {
        courseMapper.updateById(course);
        return Result.success();
    }

    public Result<?> getCourseById(Course course) {
        Course res = courseMapper.selectById(course.getId());
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getId, res.getTeacherId());
        String username = userMapper.selectOne(userLambdaQueryWrapper).getUsername();
        res.setTeacher(username);
        Integer weekTime = res.getWeekTime();
        String nameByCode = WeekEnum.getNameByCode(weekTime);
        res.setTime(nameByCode + " " + "第" + res.getDayTime() + "节");
        return Result.success(res);
    }
}
