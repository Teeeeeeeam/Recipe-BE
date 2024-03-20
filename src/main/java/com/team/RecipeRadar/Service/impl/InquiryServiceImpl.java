package com.team.RecipeRadar.Service.impl;

import com.team.RecipeRadar.Entity.Inquiry;
import com.team.RecipeRadar.dto.AddInquiryRequest;
import com.team.RecipeRadar.dto.UpdateInquiryRequest;
import com.team.RecipeRadar.repository.InquiryRepository;
import com.team.RecipeRadar.Service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    public Inquiry save(AddInquiryRequest request) {
        return inquiryRepository.save(request.toEntity());
    }

    @Override
    public List<Inquiry> findAll() {
        return inquiryRepository.findAll();
    }

    @Override
    public Inquiry findById(long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Override
    public void delete(long id) {
        inquiryRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Inquiry update(long id, UpdateInquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        inquiry.update(request.getInquiryTitle(), request.getInquiryContent());

        return inquiry;
    }

    @Override
    public Inquiry saveAnswer(AddInquiryRequest request) {
        return inquiryRepository.save(request.toEntity());
    }

    @Override
    public Inquiry saveAnswered(AddInquiryRequest request) {
        return inquiryRepository.save(request.toEntity());
    }
}
