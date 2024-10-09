package com.lhqjlb.project.util;

public class FileSizeUtil {

    public static String humanReadableSize(long sizeInBytes) {
        String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        double size = sizeInBytes;
        int unitIndex = 0;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", size, units[unitIndex]);
    }

}
