package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.user.UserAddPostDto;
import com.team.RecipeRadar.domain.post.dto.user.UserDeletePostDto;
import com.team.RecipeRadar.domain.post.exception.ex.AccessDeniedPostException;
import com.team.RecipeRadar.domain.post.exception.ex.InvalidPostRequestException;
import com.team.RecipeRadar.domain.post.exception.ex.PostNotFoundException;
import com.team.RecipeRadar.domain.post.exception.ex.UnauthorizedPostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    public Post save_post(UserAddPostDto userAddPostDto) {
        Long memberId = userAddPostDto.getMemberId();

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isPresent()) {
            Member member1 = member.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);        //yyy-dd-mm:hh-MM으로 저장 밀리세컨트는 모두 0초
            Post build = Post.builder()
                    .postTitle(userAddPostDto.getPostTitle())
                    .postContent(userAddPostDto.getPostContent())
                    .member(member1)
                    .created_at(localDateTime)
                    .build();
            return postRepository.save(build);
        } else {
            // 데이터베이스 저장 중에 문제가 발생한 경우
            throw new InvalidPostRequestException("요리글 저장에 실패했습니다.", e);
        }
    }
    @Override
    public List<Post> findAll() {
        try {
            return postRepository.findAll();
        } catch (DataAccessException e) {
            // 데이터베이스에서 모든 공지사항을 가져오는 중에 문제가 발생한 경우
            throw new PostNotFoundException("요리글 조회에 실패했습니다.", e);
        }
    }

    @Override
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없습니다." + id));
    }

    @Override
    public void delete_post(UserDeletePostDto userDeletePostDto) {

        Long memberDtoId = userDeletePostDto.getMemberId();

        Member member = getMemberThrows(memberDtoId);

        if(member.getId().equals(memberDtoId)) {
            postRepository.deleteById(member.getId());
        } else
            // 데이터베이스에서 공지사항을 삭제하는 중에 문제가 발생한 경우
            throw new AccessDeniedPostException("공지사항 삭제에 실패했습니다." , e);
    }
    @Override
    public void update_post(Long member_id, Long post_id, String postTitle, String postContent ) {

        Member member = getMemberThrows(member_id);
        Post post = postRepository.findById(post_id).orElseThrow(() -> new NoSuchElementException("해당 게시물을 찾을수 없습니다."));
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);

        if(post.getMember().equals(member)){
            post.update(postContent);
        }else
        // 업데이트하는 중에 문제가 발생한 경우
            throw new UnauthorizedPostException("요리글 수정에 실패했습니다." + id, e);
    }

    private Member getMemberThrows(Long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을수 없습니다."));
    }
}
