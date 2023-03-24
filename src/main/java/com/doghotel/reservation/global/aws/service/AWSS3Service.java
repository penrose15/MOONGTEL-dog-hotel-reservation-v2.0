package com.doghotel.reservation.global.aws.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.doghotel.reservation.global.aws.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class AWSS3Service {

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String originalFileName = originalFileName(multipartFile);
        String fileName = filename(originalFileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        try (InputStream inputStream = multipartFile.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            objectMetadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, byteArrayInputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            log.error("Can not upload image, ", e);
            throw new FileUploadException();
        }
        String url = amazonS3Client.getUrl(bucketName, fileName).toString();

        return url;
    }
    public Long fileSize(MultipartFile multipartFile) {
        try {
            return multipartFile.getSize();
        } catch (Exception e) {
            log.error("file not exist", e);
        } return -1L;
    }
    public String originalFileName(MultipartFile multipartFile) {
        try {
            return multipartFile.getOriginalFilename();
        } catch (Exception e) {
            log.error("file not exist", e);
        }
        return null;
    }

    public String filename(String filename) {
        try {
            return CommonUtils.buildFileName(filename);
        } catch (Exception e) {
            log.error("Can not filename, ", e);
            return "filename";
        }

    }

    public String deleteFile(String filename) {
        String result = "file deleted";
        try {
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, filename);
            if(isObjectExist) {
                amazonS3Client.deleteObject(bucketName, filename);
            } else {
                result = "file not found";
            }
        } catch (Exception e) {
            log.debug("delete failed", e);
        }

        return result;
    }

}

