package com.fs.starter.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fs.starter.config.minio.MinioConfig;
import com.fs.starter.domain.entity.SysFile;
import com.fs.starter.mapper.SysFileMapper;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private SysFileMapper sysFileMapper;

    public String upload(MultipartFile file) {
        try {
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            return uploadBytes(file.getBytes(), file.getOriginalFilename(), contentType);
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new RuntimeException("文件读取失败: " + e.getMessage());
        }
    }

    public String uploadBytes(byte[] data, String originalFilename, String contentType) {
        try {
            String fileHash = md5(data);

            SysFile exist = findByFileHash(fileHash);
            if (exist != null) {
                log.info("文件已存在，跳过上传: {}", exist.getUrl());
                return exist.getUrl();
            }

            String bucketName = minioConfig.getBucketName();
            ensureBucket(bucketName);

            String ext = resolveExtension(originalFilename, contentType);
            String objectName = UUID.randomUUID() + ext;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(data), data.length, -1)
                    .contentType(contentType)
                    .build());

            String url = minioConfig.getBaseUrl() + "/" + bucketName + "/" + objectName;
            log.info("文件上传成功: {}", url);

            SysFile sysFile = new SysFile();
            sysFile.setFileHash(fileHash);
            sysFile.setUrl(url);
            sysFile.setOriginalName(originalFilename);
            sysFile.setFileSize((long) data.length);
            try {
                sysFileMapper.insert(sysFile);
            } catch (DuplicateKeyException e) {
                removeObjectQuietly(bucketName, objectName);
                SysFile duplicated = findByFileHash(fileHash);
                if (duplicated != null) {
                    log.info("并发上传相同文件，复用已有记录: {}", duplicated.getUrl());
                    return duplicated.getUrl();
                }
                throw e;
            }

            return url;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    private SysFile findByFileHash(String fileHash) {
        return sysFileMapper.selectList(
                        Wrappers.<SysFile>lambdaQuery()
                                .eq(SysFile::getFileHash, fileHash)
                                .orderByAsc(SysFile::getId)
                                .last("LIMIT 1"))
                .stream()
                .findFirst()
                .orElse(null);
    }

    private void removeObjectQuietly(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.warn("清理重复上传对象失败: {}/{}", bucketName, objectName, e);
        }
    }

    private static String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        if ("image/png".equals(contentType)) {
            return ".png";
        }
        if ("image/jpeg".equals(contentType)) {
            return ".jpg";
        }
        if ("image/gif".equals(contentType)) {
            return ".gif";
        }
        if ("image/webp".equals(contentType)) {
            return ".webp";
        }
        return "";
    }

    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件访问 URL 失败: objectName={}", objectName, e);
            throw new RuntimeException("获取文件访问 URL 失败");
        }
    }

    public void delete(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build());
            log.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件删除失败: objectName={}", objectName, e);
            throw new RuntimeException("文件删除失败");
        }
    }

    private void ensureBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("创建存储桶: {}", bucketName);
        }
    }

    private String md5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算文件hash失败", e);
        }
    }
}
