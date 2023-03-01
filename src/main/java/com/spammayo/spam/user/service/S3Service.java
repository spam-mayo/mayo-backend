package com.spammayo.spam.user.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public Map<String, String> uploadFile(String profileKey, MultipartFile multipartFile, boolean isBasicImage) throws IOException {
        String fileName = multipartFile.getOriginalFilename();

        String contextType = fileName.split("\\.")[1];

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(setFileType(contextType));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dateFormat.format(new Date());
        fileName += "-" + date;

        deleteImage(profileKey, isBasicImage);

        HashMap<String, String> map = new HashMap<>();

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        map.put("key", fileName);
        map.put("url", amazonS3.getUrl(bucket, fileName).toString());

        return map;
    }

    private String setFileType(String contextType) {

        switch (contextType) {
            case "jpeg":
                contextType = "image/jpeg";
                break;
            case "jpg":
                contextType = "image/jpg";
                break;
            case "png":
                contextType = "image/png";
                break;
            case "gif":
                contextType = "image/gif";
                break;
            default:
                throw new BusinessLogicException(ExceptionCode.INVALID_VALUES);
        }

        return contextType;
    }

    private void deleteImage(String profileKey, boolean isBasicImage) {
        if (!isBasicImage) {
            boolean isExistKey = amazonS3.doesObjectExist(bucket, profileKey);

            if (isExistKey) {
                amazonS3.deleteObject(bucket, profileKey);
            }
        }
    }

}
