package com.team.RecipeRadar.domain.inquiry.application;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserAddInquiryDto;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserDeleteInquiryDto;

import java.util.List;

public interface InquiryService {
    Inquiry save(UserAddInquiryDto userAddInquiryDto);

    List<Inquiry> findAll();

    Inquiry findById(long id);

    void delete(UserDeleteInquiryDto userDeleteInquiryDto);

    void update(Long memberId, Long inquiryId, String inquiryTitle);

}