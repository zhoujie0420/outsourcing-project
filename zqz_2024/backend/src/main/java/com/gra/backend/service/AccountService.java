package com.gra.backend.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.result.Result;
import com.gra.backend.dto.UserList;
import com.gra.backend.dto.token;
import com.gra.backend.mapper.*;
import com.gra.backend.pojo.*;
import com.gra.backend.response.UserInfoRep;
import com.gra.backend.service.utils.UserDetailsImpl;
import com.gra.backend.utils.JwtUtil;
import com.gra.backend.utils.UserUtil;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gra.backend.common.constant.ResultCode.USER_HAS_EXISTED;
import static com.gra.backend.common.constant.UserRoleConstant.PATIENT_CODE;


@Service
@AllArgsConstructor
public class AccountService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JavaMailSender javaMailSender;
    public Result<?> getToken(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
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

    public Result<?> getRole(UserList userList) {
        Integer role = Integer.valueOf(userList.getUserRole());
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // string list 转 int list stream
        List<Integer> list = Arrays.stream(userList.getUserList().toArray())
                .map(Object::toString)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        userLambdaQueryWrapper.in(User::getId, list);
        userLambdaQueryWrapper.ne(User::getRole, role);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);

        List<UserInfoRep> res = new ArrayList<>();
        if (Objects.equals(role, PATIENT_CODE)) {
            users.forEach(
                    nowUser -> {
                        UserInfoRep userInfoRep = new UserInfoRep();
                        BeanUtil.copyProperties(nowUser, userInfoRep);
                        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        doctorLambdaQueryWrapper.eq(Doctor::getId, userInfoRep.getRoleId());
                        Doctor doctor = doctorMapper.selectOne(doctorLambdaQueryWrapper);
                        Integer departmentId = doctor.getDepartmentId();
                        LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        departmentLambdaQueryWrapper.eq(Department::getId, departmentId);
                        Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
                        userInfoRep.setDepartmentName(department.getDepartmentName());
                        res.add(userInfoRep);
                    }
            );
        } else {
            users.forEach(
                    nowUser -> {
                        UserInfoRep userInfoRep = new UserInfoRep();
                        BeanUtil.copyProperties(nowUser, userInfoRep);
                        res.add(userInfoRep);
                    }
            );
        }
        return Result.success(res);
    }




}

