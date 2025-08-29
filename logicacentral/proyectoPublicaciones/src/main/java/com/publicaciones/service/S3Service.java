package com.publicaciones.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;


// SERVICIO CLIENTE S3
@Service
public class S3Service {

    private final S3Client s3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public S3Service(@Value("${aws.accessKeyId}") String accessKey,
                     @Value("${aws.secretAccessKey}") String secretKey,
                     @Value("${aws.s3.region}") String region) {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public String subirArchivo(String key, byte[] contenido) {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .contentType("image/png") 
                        .build(),
                RequestBody.fromBytes(contenido)
        );
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }
    public void eliminarArchivo(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        );
    }
}