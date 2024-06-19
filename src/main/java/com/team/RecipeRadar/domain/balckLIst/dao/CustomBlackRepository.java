package com.team.RecipeRadar.domain.balckLIst.dao;

import com.team.RecipeRadar.domain.balckLIst.dto.BlackListDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomBlackRepository {

    Slice<BlackListDto> allBlackList(Long lastId, Pageable pageable);

    Slice<BlackListDto> searchEmailBlackList(String email,Long lastId,Pageable pageable);
}
