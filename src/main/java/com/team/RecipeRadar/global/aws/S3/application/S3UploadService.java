package com.team.RecipeRadar.global.aws.S3.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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
            throw new BadRequestException("대표 이미지를 등록해주세요");
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
            throw new BadRequestException("파일업로드 실패");
        }
        return storeFile;
    }

    /**
     * 버킷에서 이미지 삭제
     * @param uploadFileName  업로드된 파일명
     */
    public void deleteFile(String uploadFileName){
        try{
            amazonS3.deleteObject(bucket,uploadFileName);
        }catch (SdkClientException e){
            throw new BadRequestException("파일 삭제도중 오류 발생");
        }
    }

    private String createStoreFile(String originalFilename) {
        int lastIndexOf = originalFilename.lastIndexOf(".");                    // 마지막 종류
        String substring = originalFilename.substring(lastIndexOf + 1);     //확장자 종류

        String extension = substring.toLowerCase();
        if (!extension.equals("jpeg") && !extension.equals("jpg") && !extension.equals("png")) {
            throw new BadRequestException("이미지 파일만 등록 해주세요");
        }

        String uuid = UUID.randomUUID().toString();
        return uuid+"."+substring;
    }
}
