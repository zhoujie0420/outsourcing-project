package com.gra.backend.controller;

import com.gra.backend.common.result.Result;
import com.gra.backend.dto.AppointmentReq;
import com.gra.backend.pojo.Appointment;
import com.gra.backend.pojo.User;
import com.gra.backend.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user/appointment")
public class AppointmentController {

    @Resource
    private AppointmentService appointmentService;

    @GetMapping("getAppointmentUsers")
    public Result<?> getAppointmentUsers() {
        return appointmentService.getAppointmentUsers();
    }

    @PostMapping("getAppointmentsOfDoctor")
    public Result<?> getAppointmentsOfDoctor(AppointmentReq appointmentReq) {
        return appointmentService.getAppointmentsOfDoctor(appointmentReq);
    }

    @PostMapping("addAppointment")
    public Result<?> addAppointment(Appointment appointmentReq) {
        return appointmentService.addAppointment(appointmentReq);
    }


    @PostMapping("updateAppointment")
    public Result<?> updateAppointment(Appointment appointmentReq) {
        return appointmentService.updateAppointment(appointmentReq);
    }
}
