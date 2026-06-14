package com.fs.starter.config.minio;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 文件存储配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /** 服务地址 */
    private String endpoint;

    /** 访问密钥 */
    private String accessKey;

    /** 秘密密钥 */
    private String secretKey;

    /** 存储桶名称 */
    private String bucketName;

    /** 文件访问基础地址（公共域名） */
    private String baseUrl;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
