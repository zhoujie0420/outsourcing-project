package com.lhqjlb.project.util;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;

public class MultipartUtil {

    public static boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return StrUtil.isNotBlank(contentType) && contentType.toLowerCase().startsWith("multipart/");
    }
}
