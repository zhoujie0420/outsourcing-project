package com.gra.backend.controller;


import com.gra.backend.service.OssServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Oss相关操作接口
 */
@RestController
@RequestMapping("/api/aliyun/oss")
public class OssController {
    @Resource
    private OssServiceImpl ossService;

    @PostMapping("/upload")
    public String upload(@RequestPart("file") MultipartFile file) {
        String url = ossService.uploadFile(file);
        return url;
    }


    @PostMapping("/uploadImages")
    public String uploadImages(@RequestPart("file") MultipartFile file) {
        // 获取上传头像的文件 MultipartFile
        // 返回上传的oss路径
        String url = ossService.uploadImages(file);
        return url;
    }

}