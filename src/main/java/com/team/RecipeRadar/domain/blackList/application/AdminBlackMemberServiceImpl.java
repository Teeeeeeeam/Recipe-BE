package com.team.RecipeRadar.domain.blackList.application;

import com.team.RecipeRadar.domain.blackList.domain.BlackList;
import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.blackList.dto.BlackListDto;
import com.team.RecipeRadar.domain.blackList.dto.response.BlackListResponse;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminBlackMemberServiceImpl implements AdminBlackMemberService {

    private final BlackListRepository blackListRepository;

    /**
     * 블랙리스트 저장된 모든 정보를 조회하는 메서드
     */
    @Override
    public BlackListResponse getBlackList(Long lastId, Pageable pageable) {
        Slice<BlackListDto> blackListDtoList = blackListRepository.allBlackList(lastId, pageable);
        return new BlackListResponse(blackListDtoList.hasNext(),blackListDtoList.getContent());
    }

    /**
     * 블랙리스트 임시 차단 메서드
     * 블랙리스트의 대해서 임시적으로 관리자가 차단/해제를 할수 있습니다.
     */
    @Override
    public boolean temporarilyUnblockUser(Long blackId) {
        BlackList blackList = blackListRepository.findById(blackId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_EMAIL));
        blackList.unLock(blackList.isBlack_check());
        BlackList update_black = blackListRepository.save(blackList);

        return update_black.isBlack_check();
    }

    /**
     * 블랙리스트를 해제하는 메서드
     */
    @Override
    public void deleteBlackList(Long blackId) {
        blackListRepository.deleteById(blackId);
    }

    /**
     * 블랙리스트된 이메일을 조회하는 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public BlackListResponse searchEmailBlackList(String email, Long lastId, Pageable pageable) {
        Slice<BlackListDto> searchEmailBlackList = blackListRepository.searchEmailBlackList(email, lastId, pageable);
        return new BlackListResponse(searchEmailBlackList.hasNext(),searchEmailBlackList.getContent());
    }


}
