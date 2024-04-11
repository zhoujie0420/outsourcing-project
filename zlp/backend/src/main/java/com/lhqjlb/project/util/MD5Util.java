package com.lhqjlb.project.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class MD5Util {

    public static String encrypt(String input) {
        try {
            // 创建MD5消息摘要对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将字符串转换为字节数组
            byte[] inputBytes = input.getBytes();

            // 计算MD5摘要
            byte[] md5Bytes = md.digest(inputBytes);

            // 将字节数组转换为字符串
            StringBuilder result = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                result.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
            }

            return result.toString().toLowerCase(); // 返回小写形式的MD5字符串
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5加密失败 {}",e.getMessage());
            return null; // 处理算法不存在的异常
        }
    }

    public static void main(String[] args) {
        String input = "123456";
        String encrypted = encrypt(input);
        System.out.println("Original: " + input);
        System.out.println("MD5: " + encrypted);
    }
}
