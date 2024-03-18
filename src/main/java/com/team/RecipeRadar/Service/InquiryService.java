package com.team.RecipeRadar.service;

import com.team.RecipeRadar.Entity.Inquiry;
import com.team.RecipeRadar.dto.AddInquiryRequest;
import com.team.RecipeRadar.dto.UpdateInquiryRequest;
import com.team.RecipeRadar.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public Inquiry save(AddInquiryRequest request) {
        return inquiryRepository.save(request.toEntity());
    }

    public List<Inquiry> findAll() {
        return inquiryRepository.findAll();
    }

    public Inquiry findById(long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        inquiryRepository.deleteById(id);
    }

    @Transactional
    public Inquiry update(long id, UpdateInquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        inquiry.update(request.getInquiryTitle(), request.getInquiryContent());

        return inquiry;
    }
}
