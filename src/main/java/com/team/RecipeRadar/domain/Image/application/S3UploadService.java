package com.team.RecipeRadar.domain.Image.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.img.ImageErrorType;
import com.team.RecipeRadar.global.exception.ex.img.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.team.RecipeRadar.global.exception.ex.img.ImageErrorType.*;

@Service
@Transactional
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    /**
     * s3에 이미지 업로드
     * @param file
     * @return
     */
    public String uploadFile(MultipartFile file){

        if (file.isEmpty()|| file==null){
            throw new ImageException(MISSING_PRIMARY_IMAGE);
        }

        String originalFilename = file.getOriginalFilename();
        String storeFile = createStoreFile(originalFilename);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try{
            InputStream inputStream = file.getInputStream();
            amazonS3.putObject(new PutObjectRequest(bucket,storeFile,inputStream,objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        }catch (IOException e) {
            e.printStackTrace();
            throw new ImageException(UPLOAD_FAILS);
        }
        return storeFile;
    }

    /**
     * 버킷에서 이미지 삭제
     * @param uploadFileName  업로드된 파일명
     */
    public void deleteFile(String uploadFileName){
        try{
            if(uploadFileName.startsWith("https")) {
                amazonS3.deleteObject(bucket, uploadFileName);
            }
        }catch (SdkClientException e){
            throw new ImageException(UPLOAD_FAILS);
        }
    }

    private String createStoreFile(String originalFilename) {
        int lastIndexOf = originalFilename.lastIndexOf(".");                    // 마지막 종류
        String substring = originalFilename.substring(lastIndexOf + 1);     //확장자 종류

        String extension = substring.toLowerCase();
        if (!extension.equals("jpeg") && !extension.equals("jpg") && !extension.equals("png")) {
            throw new ImageException(INVALID_IMAGE_FORMAT);
        }

        String uuid = UUID.randomUUID().toString();
        return uuid+"."+substring;
    }
}
