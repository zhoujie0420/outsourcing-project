package com.gra.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.result.Result;
import com.gra.backend.dto.AppointmentReq;
import com.gra.backend.mapper.*;
import com.gra.backend.pojo.*;
import com.gra.backend.utils.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppointmentService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private AppointmentMapper appointmentMapper;
    @Resource
    private SignMapper signMapper;



    public Result<?> getAppointmentUsers() {
        User user = UserUtil.getUser();
        if (user.getRole() == 1) {
            LambdaQueryWrapper<Sign> signLambdaQueryWrapper = new LambdaQueryWrapper<>();
            signLambdaQueryWrapper.eq(Sign::getPatientId, user.getId()).eq(Sign::getStatus, 2);
            List<Sign> signs = signMapper.selectList(signLambdaQueryWrapper);
            List<User> res = new ArrayList<>();
            for (Sign sign : signs) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getId, sign.getDoctorId());
                User doctor = userMapper.selectOne(userLambdaQueryWrapper);
                LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
                doctorLambdaQueryWrapper.eq(Doctor::getId, doctor.getRoleId());
                Doctor doctorInfo = doctorMapper.selectOne(doctorLambdaQueryWrapper);
                Integer departmentId = doctorInfo.getDepartmentId();
                LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                departmentLambdaQueryWrapper.eq(Department::getId, departmentId);
                Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
                User resUser = new User();
                resUser.setOtherName(doctor.getUsername());
                resUser.setDepartmentName(department.getDepartmentName());
                resUser.setDepartmentDes(department.getDescription());
                resUser.setOtherId(doctor.getId());
                res.add(resUser);
            }
            return Result.success(res);
        } else if (user.getRole() == 2){
            LambdaQueryWrapper<Appointment> appointmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            appointmentLambdaQueryWrapper.eq(Appointment::getDoctorId, user.getId()).eq(Appointment::getStatus, 1);
            List<Appointment> appointments = appointmentMapper.selectList(appointmentLambdaQueryWrapper);
            for (Appointment appointment : appointments) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getId, appointment.getPatientId());
                User patient = userMapper.selectOne(userLambdaQueryWrapper);
                appointment.setOtherName(patient.getUsername());
            }
            return Result.success(appointments);
        }else{
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
            LambdaQueryWrapper<Appointment> appointmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            appointmentLambdaQueryWrapper.eq(Appointment::getStatus, 2).in(Appointment::getDoctorId, res);
            List<Appointment> appointments = appointmentMapper.selectList(appointmentLambdaQueryWrapper);
            for (Appointment appointment : appointments) {
                LambdaQueryWrapper<User> newUserQw = new LambdaQueryWrapper<>();
                newUserQw.eq(User::getId, appointment.getPatientId());
                User patient = userMapper.selectOne(newUserQw);
                appointment.setOtherName(patient.getUsername());
                LambdaQueryWrapper<User> newUserQwTwo = new LambdaQueryWrapper<>();
                newUserQwTwo.eq(User::getId, appointment.getDoctorId());
                User doctor = userMapper.selectOne(newUserQwTwo);
                appointment.setOtherNameTwo(doctor.getUsername());
            }
            return Result.success(appointments);
        }
    }

    public Result<?> getAppointmentsOfDoctor(AppointmentReq appointmentReq) {
        Integer id = appointmentReq.getId();
        String data = appointmentReq.getDate();
        LambdaQueryWrapper<Appointment> appointmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        appointmentLambdaQueryWrapper.eq(Appointment::getDoctorId, id).eq(Appointment::getAppointmentDate, data);
        List<Appointment> appointments = appointmentMapper.selectList(appointmentLambdaQueryWrapper);
        for(int i = 1;i <= 9; ++i){
            boolean flag = false;
            for(Appointment appointment : appointments){
                if(appointment.getTimeSlot() == i){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                Appointment appointment = new Appointment();
                appointment.setDoctorId(id);
                appointment.setAppointmentDate(data);
                appointment.setTimeSlot(i);
                appointment.setStatus(0);
                appointments.add(appointment);
            }
        }
        return Result.success(appointments);
    }


    public Result<?> addAppointment(Appointment appointment) {
        User user = UserUtil.getUser();
        appointment.setPatientId(user.getId());
        appointment.setStatus(1);
        appointmentMapper.insert(appointment);
        return Result.success();
    }

    public Result<?> updateAppointment(Appointment appointment) {
        appointment.setStatus(2);
        appointmentMapper.updateById(appointment);
        return Result.success();
    }
}
