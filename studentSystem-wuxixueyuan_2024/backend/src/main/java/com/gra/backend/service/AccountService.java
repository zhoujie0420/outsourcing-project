package com.gra.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.result.Result;
import com.gra.backend.dto.token;
import com.gra.backend.mapper.*;
import com.gra.backend.pojo.Course;
import com.gra.backend.pojo.StudentCourse;
import com.gra.backend.pojo.User;
import com.gra.backend.service.utils.UserDetailsImpl;
import com.gra.backend.utils.JwtUtil;
import com.gra.backend.utils.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.gra.backend.common.constant.ResultCode.USER_HAS_EXISTED;


@Service
@AllArgsConstructor
public class AccountService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private StudentCourseMapper studentCourseMapper;

    public Result<?> getToken(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken); //登录失败会自己处理
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User res = loginUser.getUser(); // 取出user
        String jwt = JwtUtil.createJWT(res.getId().toString());
        return Result.success(new token(jwt));
    }

    public Result<?> getInfo() {
        User user = UserUtil.getUser();
        user.setPassword(null);
        return Result.success(user);
    }

    public Result<?> register(User user) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, user.getUsername());
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        if (!users.isEmpty()) {
            return Result.fail(USER_HAS_EXISTED.getMessage());
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setRole(1);
        userMapper.insert(user);
        return Result.success();
    }

    public Result<?> getUserList() {
        User user = UserUtil.getUser();
        if (user.getRole() == 0) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.ne(User::getRole, 0).orderBy(true, true, User::getRole);
            List<User> users = userMapper.selectList(userLambdaQueryWrapper);
            return Result.success(users);
        } else if (user.getRole() == 2) {
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.eq(Course::getTeacherId, user.getId());
            List<Integer> courseIds = courseMapper.selectList(courseLambdaQueryWrapper).stream().map(Course::getId).collect(Collectors.toList());
            LambdaQueryWrapper<StudentCourse> studentCourseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentCourseLambdaQueryWrapper.in(StudentCourse::getCourseId, courseIds);
            List<Integer> studentIds = studentCourseMapper.selectList(studentCourseLambdaQueryWrapper).stream().map(StudentCourse::getStudentId).collect(Collectors.toList());
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.in(User::getId, studentIds);
            List<User> users = userMapper.selectList(userLambdaQueryWrapper);
            return Result.success(users);
        } else {
            return Result.fail("权限不足");
        }
    }

    public Result<?> addUser(User user) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, user.getUsername());
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        if (!users.isEmpty()) {
            return Result.fail(USER_HAS_EXISTED.getMessage());
        }
        String encodePassword = passwordEncoder.encode(user.getUsername());
        user.setPassword(encodePassword);
        user.setStatus(1);
        userMapper.insert(user);
        return Result.success();
    }

    public Result<?> updateUserStatus(User user) {
        User newUser = userMapper.selectById(user.getId());
        if(newUser.getStatus() == 1){
            newUser.setStatus(0);
        }else {
            newUser.setStatus(1);
        }
        userMapper.updateById(newUser);
        return Result.success();
    }

    public Result<?> getTeaByCourseId(Course course) {
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseLambdaQueryWrapper.eq(Course::getId, course.getId());
        Course course1 = courseMapper.selectOne(courseLambdaQueryWrapper);
        User user = userMapper.selectById(course1.getTeacherId());
        return Result.success(user);
    }
}

