package com.vnd.demominio.api;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.xmlpull.v1.XmlPullParserException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/files")
public class FileApi {

    private final MinioClient minioClient;

    public FileApi(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @GetMapping("{bucketName}/**")
    public ResponseEntity<Resource> downloadFile(@PathVariable("bucketName") String bucketName,
                                                 HttpServletRequest request)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException,
            InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException,
            XmlPullParserException, ErrorResponseException {

        String path = String.valueOf(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        String objectName = path.substring(path.indexOf(bucketName) + bucketName.length() + 1);

        InputStream objectStream = minioClient.getObject(bucketName, objectName);
        String fileName = objectName.substring(objectName.lastIndexOf('/') + 1);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(new InputStreamResource(objectStream));
    }

    @PostMapping("/{bucketName}")
    public void uploadFile(@PathVariable("bucketName") String bucketName,
                           @RequestParam("objectName") String objectName,
                           @RequestParam("file") MultipartFile file) throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException,
            NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, RegionConflictException {

        if (!minioClient.bucketExists(bucketName)) {
            minioClient.makeBucket(bucketName);
        }

        minioClient.putObject(bucketName, objectName, file.getInputStream(), file.getContentType());
    }
}
