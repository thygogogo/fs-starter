package com.fs.starter.admin.controller;

import com.fs.starter.common.result.R;
import com.fs.starter.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口（后台管理端）
 */
@RestController
@RequestMapping("/admin/file")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问 URL
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        return R.ok(minioService.upload(file));
    }

    /**
     * 删除文件
     *
     * @param objectName 文件对象名
     */
    @DeleteMapping("/delete")
    public R<Void> delete(@RequestParam String objectName) {
        minioService.delete(objectName);
        return R.ok();
    }
}
