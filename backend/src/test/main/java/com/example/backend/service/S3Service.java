package com.example.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.config.S3Config;
import com.example.backend.exception.storage.StorageError;
import com.example.backend.exception.storage.StorageException;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3Service {
    private final S3Client s3Client;
    private final S3Config s3Config;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public S3Service(S3Config s3Config, S3Client s3Client){
        this.s3Client = s3Client;
        this.s3Config = s3Config;
    }

    public void uploadFile(String key, MultipartFile file){
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(s3Config.getS3Properties().getBucket())
            .key(key)
            .contentType(file.getContentType())
            .build();
        try{
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch(Exception e){
            logger.error(e.getMessage());
            throw new StorageException(StorageError.UPLOAD_FILE_FAILED);
        }
    }

    public byte[] getFileContent(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(s3Config.getS3Properties().getBucket())
            .key(key).build();
        try {
            return s3Client.getObjectAsBytes(request).asByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new StorageException(StorageError.GET_FILE_FAILED);
        }
    }

    public void deleteFile(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(s3Config.getS3Properties().getBucket())
            .key(key)
            .build();
        try {
            s3Client.deleteObject(request);
        } catch (Exception e) {
            logger.error("Failed to delete S3 object: {}", key, e);
            throw new StorageException(StorageError.DELETE_FILE_FAILED);
        }
    }
}
