package com.team.RecipeRadar.domain.Image.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.exception.ex.img.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.team.RecipeRadar.global.exception.ex.img.ImageErrorType.*;

@Service
@Transactional
@RequiredArgsConstructor
public class S3UploadService{

    private final AmazonS3 amazonS3;
    private final ImgRepository imgRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    
    /**
     * s3에 이미지 업로드 하는 메서드
     */
    public <T>String uploadFile(MultipartFile file,List<T> entities){

        if (file.isEmpty()|| file==null){
            throw new ImageException(MISSING_PRIMARY_IMAGE);
        }

        // 원본 파일명과 저장될 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String storeFile = createStoreFile(originalFilename);

        // 객체 메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        // 연결된 엔티티가 있으면 저장
        List<Object> entitiesToSave = new ArrayList<>();
        if(!entities.isEmpty()) {
            entities.forEach(entity -> {
                if (entity instanceof Recipe || entity instanceof Post || entity instanceof Notice || entity instanceof Question) {
                    entitiesToSave.add(entity);
                }
            });
            imgRepository.save(UploadFile.createUploadFile(entitiesToSave, file.getOriginalFilename(), storeFile));
        }

        // 파일을 S3에 업로드
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
     * S3 이미지를 삭제하는 메서드
     * 저장된 파일명을 사용해 S3 버킷에서 해당 이미지를 삭제
     */
    public void deleteFile(String uploadFileName){
        try{
            amazonS3.deleteObject(bucket, uploadFileName);
        }catch (SdkClientException e){
            throw new ImageException(UPLOAD_FAILS);
        }
    }

    /**
     * 이미지를 업데이트 하는 메서드
     */
    public <T>void updateFile(MultipartFile file,List<T> entities){
        UploadFile uploadFile = null;

        // 각 엔티티 타입에 따라 업로드된 파일 조회
        for(T entity : entities){
            if (entity instanceof Post) {
                uploadFile = imgRepository.findByPostId(((Post) entity).getId());
            } else if (entity instanceof Notice) {
                uploadFile = imgRepository.findByNoticeId(((Notice) entity).getId());
            }else if(entity instanceof Recipe){
                uploadFile = imgRepository.findUploadFileByRecipeIdAndPostNull(((Recipe) entity).getId()).get();
            }else if(entity instanceof Question)
                uploadFile = imgRepository.findByQuestionId(((Question) entity).getId());
        }

        // 이미지 업로드 또는 업데이트 수행
        saveOrUpdateUploadFile(file, List.of(), uploadFile);
    }

    /* 이미지를 저장하거나 업데이트하는 메서드 */
    private <T> void saveOrUpdateUploadFile(MultipartFile file, T entity, UploadFile uploadFile) {
        if (file != null && !file.isEmpty()) {
            String storedFileName = uploadFile(file,List.of());
            if (uploadFile != null) {
                // 파일명이 다를 경우 기존 파일 삭제 후 업데이트
                if (!uploadFile.getOriginFileName().equals(file.getOriginalFilename())) {
                    deleteFile(uploadFile.getStoreFileName());
                    uploadFile.update(storedFileName, file.getOriginalFilename());
                }
            } else {
                // 공지사항 및 질문만 최초 등록시 이미지값이 필수가 아니라 등록기능 작성
                imgRepository.save(UploadFile.createUploadFile(List.of(entity), file.getOriginalFilename(), storedFileName));
                uploadFile(file,List.of());
            }
        }
    }

    /* 저장될 파일명 생성 */
    private String createStoreFile(String originalFilename) {
        int lastIndexOf = originalFilename.lastIndexOf(".");                    // 마지막 종류
        String extension = originalFilename.substring(lastIndexOf + 1).toLowerCase();     //확장자 종류

        Set<String> allowedExtensions = Set.of("jpeg", "jpg", "png");
        if (!allowedExtensions.contains(extension)) {
            throw new ImageException(INVALID_IMAGE_FORMAT);
        }

        String uuid = UUID.randomUUID().toString();
        return uuid+"."+extension;
    }
}
