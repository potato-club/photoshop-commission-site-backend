package com.community.site.service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.community.site.error.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.community.site.error.ErrorCode.ACCESS_DENIED_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class S3UploadService {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public String uploadFile(MultipartFile multipartFile) {

        validateFileExists(multipartFile);
        String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucket, s3FileName, inputStream, objMeta)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new UnAuthorizedException("FILE_UPLOAD_FAILED", ACCESS_DENIED_EXCEPTION);
        }

        return s3Client.getUrl(bucket, s3FileName).toString();
    }

    // 파일 유효성 검사
    @Transactional
    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new UnAuthorizedException("FILE_EMPTY", ACCESS_DENIED_EXCEPTION);
        }
    }

    // 업로드된 파일 Url 가져오기
    @Transactional
    public String getFileUrl(String fileName) {
        return s3Client.getUrl(bucket, fileName).toString();
    }

    // DeleteObject를 통해 S3 파일 삭제
    @Transactional
    public void deleteFile(String fileUrl) {
        log.info("deleteImage = {}", fileUrl);
        s3Client.deleteObject(new DeleteObjectRequest(bucket, fileUrl));
    }
}
