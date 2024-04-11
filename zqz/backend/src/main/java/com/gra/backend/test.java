package com.gra.backend;

import lombok.Value;
import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
public class test {

    private final JavaMailSender mailSender;

    public test(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    // 配置文件中我的qq邮箱

    @Test
    public void sendMail() throws MessagingException, UnsupportedEncodingException {

    }
}
