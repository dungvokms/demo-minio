package com.vnd.demominio.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${app.minio.host}")
    private String minioHost;

    @Value("${app.ninio.accessKey}")
    private String minioAccessKey;

    @Value("${app.minio.secretKey}")
    private String minioSecretKey;

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(minioHost, minioAccessKey, minioSecretKey);
    }
}
