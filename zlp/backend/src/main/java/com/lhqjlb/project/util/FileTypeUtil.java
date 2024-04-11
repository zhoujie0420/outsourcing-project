package com.lhqjlb.project.util;

public class FileTypeUtil {

    private static final String IMAGE_TYPE = "image";
    private static final String VIDEO_TYPE = "video";
    private static final String AUDIO_TYPE = "audio";

    public static String getFileType(String mimeType) {
        if (mimeType == null){
            return "文件";
        }
        if (mimeType.startsWith(IMAGE_TYPE)) {
            return "图片";
        } else if (mimeType.startsWith(VIDEO_TYPE)) {
            return "视频";
        } else if (mimeType.startsWith(AUDIO_TYPE)) {
            return "音频";
        } else {
            return "文件";
        }
    }

}