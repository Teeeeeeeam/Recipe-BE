package com.team.RecipeRadar.domain.inquiry.dao;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
