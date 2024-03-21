package com.team.RecipeRadar.domain.inquiry.application;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.inquiry.dto.AddInquiryRequest;
import com.team.RecipeRadar.domain.inquiry.dto.UpdateInquiryRequest;

import java.util.List;

public interface InquiryService {
    Inquiry save(AddInquiryRequest request);

    List<Inquiry> findAll();

    Inquiry findById(long id);

    void delete(long id);

    Inquiry update(long id, UpdateInquiryRequest request);

    Inquiry saveAnswer(AddInquiryRequest request);

    Inquiry saveAnswered(AddInquiryRequest request);
}