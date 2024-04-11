package com.gra.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.result.Result;
import com.gra.backend.mapper.DoctorMapper;
import com.gra.backend.mapper.SignMapper;
import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.Doctor;
import com.gra.backend.pojo.Sign;

import com.gra.backend.pojo.User;
import com.gra.backend.utils.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SignService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private SignMapper signMapper;
    @Resource
    private DoctorMapper doctorMapper;

    public Result<?> addSign(Sign signRecord) {
        //判断userId是医生还是患者
        User user = UserUtil.getUser();
        signRecord.setPatientId(user.getId());
        signRecord.setDoctorId(signRecord.getDoctorId());
        signRecord.setStatus(0);
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
        String formattedDateTime = now.format(formatter);
        signRecord.setCreateTime(formattedDateTime);
        signMapper.insert(signRecord);
        return Result.success();
    }

    public Result<?> getSign() {
        User user = UserUtil.getUser();
        if (user == null) {
            return Result.fail("用户未登录");
        }
        Integer role = user.getRole();
        LambdaQueryWrapper<Sign> recordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recordLambdaQueryWrapper.orderByDesc(Sign::getCreateTime);
        List<Sign> signRecords = new ArrayList<>();
        if (role == 3) {
            LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            doctorLambdaQueryWrapper.eq(Doctor::getHosId, user.getRoleId());
            List<Doctor> doctors = doctorMapper.selectList(doctorLambdaQueryWrapper);
            //获取doctors的id,stream流
            List<Integer> doctorIds = doctors.stream().map(Doctor::getId).collect(Collectors.toList());
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.in(User::getRoleId, doctorIds);
            userLambdaQueryWrapper.eq(User::getRole, 2);
            List<User> doctorUsers = userMapper.selectList(userLambdaQueryWrapper);
            List<Integer> res = doctorUsers.stream().map(User::getId).collect(Collectors.toList());
            recordLambdaQueryWrapper.in(Sign::getDoctorId, res);
            signRecords = signMapper.selectList(recordLambdaQueryWrapper);
            for (Sign signRecord : signRecords) {
                LambdaQueryWrapper<User> newUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                newUserLambdaQueryWrapper.eq(User::getId, signRecord.getPatientId());
                User patient = userMapper.selectOne(newUserLambdaQueryWrapper);
                signRecord.setOtherName(patient.getUsername());
            }
        } else if (role == 2) {
            recordLambdaQueryWrapper.eq(Sign::getDoctorId, user.getId());
            signRecords = signMapper.selectList(recordLambdaQueryWrapper);
            for (Sign signRecord : signRecords) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getId, signRecord.getPatientId());
                User patient = userMapper.selectOne(userLambdaQueryWrapper);
                signRecord.setOtherName(patient.getUsername());
            }
        } else if (role == 1) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getRole, 2);
            List<User> doctors = userMapper.selectList(userLambdaQueryWrapper);
            for (User doctor : doctors) {
                Integer id = doctor.getId();
                LambdaQueryWrapper<Sign> signRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
                signRecordLambdaQueryWrapper.eq(Sign::getDoctorId, id);
                signRecordLambdaQueryWrapper.eq(Sign::getPatientId, user.getId());
                Sign signRecord = signMapper.selectOne(signRecordLambdaQueryWrapper);
                if (signRecord != null) {
                    signRecord.setOtherName(doctor.getUsername());
                    signRecords.add(signRecord);
                } else {
                    signRecord = new Sign();
                    signRecord.setId(0);
                    signRecord.setOtherName(doctor.getUsername());
                    signRecord.setDoctorId(id);
                    signRecord.setPatientId(user.getId());
                    signRecord.setStatus(0);
                    signRecord.setCreateTime("未开始签约流程");
                    signRecords.add(signRecord);
                }
            }
        }
        //signRecords 根据createTime排序
        Stream<Sign> sorted = signRecords.stream().sorted(Comparator.comparing(Sign::getCreateTime));
        return Result.success(sorted);
    }

    public Result<?> updateSign(Sign signRecord) {
        if (signRecord.getId() == 0) {
            addSign(signRecord);
        }
        Integer role = UserUtil.getUser().getRole();
        if (role == 3) {
            signRecord.setStatus(2);
        } else if (role == 2) {
            signRecord.setStatus(1);
        } else if (role == 1) {
            signRecord.setStatus(0);
        }
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
        String formattedDateTime = now.format(formatter);
        signRecord.setCreateTime(formattedDateTime);
        signMapper.updateById(signRecord);
        return Result.success();
    }
}