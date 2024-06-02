package com.team.RecipeRadar.domain.inquiry.application;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.inquiry.dto.user.InquiryResponse;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserAddRequest;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserUpdateRequest;
import com.team.RecipeRadar.domain.inquiry.dto.user.ValidInquiryRequest;
import org.springframework.data.domain.Pageable;


public interface InquiryService {
    void save(UserAddRequest userAddInquiryDto);

    void delete(String loginId, Long inquiryId);

    void update(Long inquiryId, UserUpdateRequest updateInquiryDto, String loginId);

    boolean validInquiryPassword(String login, ValidInquiryRequest request);

}