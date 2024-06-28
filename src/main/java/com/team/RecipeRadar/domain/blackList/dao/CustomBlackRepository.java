package com.team.RecipeRadar.domain.blackList.dao;

import com.team.RecipeRadar.domain.blackList.dto.BlackListDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomBlackRepository {

    Slice<BlackListDto> allBlackList(Long lastId, Pageable pageable);

    Slice<BlackListDto> searchEmailBlackList(String email,Long lastId,Pageable pageable);
}
