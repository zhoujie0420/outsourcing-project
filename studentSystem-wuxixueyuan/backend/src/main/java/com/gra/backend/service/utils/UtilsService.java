package com.gra.backend.service.utils;


import com.gra.backend.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UtilsService {
    private final JavaMailSender mailSender;

    public UtilsService(JavaMailSender emailSender){
        this.mailSender = emailSender;
    }
    public void sendEmail(){
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom("zzac0420@163.com");
        //邮件接收人
        message.setTo("2601069845@qq.com");
        //邮件主题
        message.setSubject("随诊提醒");
        //邮件内容
        message.setText("请进入系统查看，患者随诊预约");
        //发送邮件
        mailSender.send(message);

    }


}
