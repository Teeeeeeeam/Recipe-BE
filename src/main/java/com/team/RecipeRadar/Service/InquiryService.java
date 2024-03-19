package com.team.RecipeRadar.service;

import com.team.RecipeRadar.Entity.Inquiry;
import com.team.RecipeRadar.dto.AddInquiryRequest;
import com.team.RecipeRadar.dto.UpdateInquiryRequest;

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