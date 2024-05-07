package com.team.RecipeRadar.global.Image.utils;

import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.File;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String fileName){
        return fileDir + fileName;
    }


    /**
     * 이미지 사진을 등록하는 로직
     * @param file      이미지명
     * @return
     * @throws Exception
     */
    public UploadFile storeFile(MultipartFile file) throws Exception{

        if (file==null){
            throw new BadRequestException("사진 대표 사진을 등록해주세요");
        }
        if (file.getSize()>70*1024*1024){
            throw new BadRequestException("70MB 이하로 등록해주세요");
        }
        String originalFilename = file.getOriginalFilename();       //원본 파일명
        String storedFileName = createStoreFile(originalFilename);      // 저장된 고유한 이미지명
        file.transferTo(new File(getFullPath(storedFileName)));         // 파일을 저장

        return new UploadFile(originalFilename, storedFileName);
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
