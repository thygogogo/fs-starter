package com.fs.starter.service.util;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    public static String getExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex <= 0 || lastDotIndex >= filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}
