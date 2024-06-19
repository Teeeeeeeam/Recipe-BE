package com.team.RecipeRadar.domain.balckLIst.api;

import com.team.RecipeRadar.domain.balckLIst.application.AdminBlackMemberService;
import com.team.RecipeRadar.domain.balckLIst.dto.BlackListResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class BlackListController {

    private final AdminBlackMemberService blackMemberService;

    @Operation(summary = "블랙 리스트 이메일 조회",description = "블랙 리스트에 등록된 이메일 목록을 조회하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"blackList\":[{\"id\":1,\"email\":\"user1@example.com\",\"black_check\":true}]}}")))
    })
    @GetMapping("/black")
    public ResponseEntity<ControllerApiResponse> getBlackList(@RequestParam(name = "lastId",required = false) Long lastId,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable){
        BlackListResponse blackList = blackMemberService.getBlackList(lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",blackList));
    }


    @Operation(summary = "블랙 리스트 이메일 차단 유뮤",description = "블랙 리스트에 등록된 이메일의 차단 해제 유무를 설정하는 API(false - 임시 차단 해제 , ture - 임시 차단)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"임시 차단 해제\"}")))
    })
    @PostMapping("/blacklist/temporary-unblock/{blackId}")
    public ResponseEntity<ControllerApiResponse> unBlock(@Schema(example = "1")@PathVariable(value = "blackId") Long blackId){
        boolean unblockUser = blackMemberService.temporarilyUnblockUser(blackId);
        return ResponseEntity.ok(new ControllerApiResponse<>(unblockUser,"임시 차단 유뮤"));
    }


    @Operation(summary = "블랙 리스트 이메일 해제",description = "블랙 리스트에 등록된 이메일의 차단을 해제하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"삭제 성공\"}")))
    })
    @DeleteMapping("/blacklist/{blackId}")
    public ResponseEntity<ControllerApiResponse> deleteBlack(@PathVariable(value = "blackId") Long blackId){
        blackMemberService.deleteBlackList(blackId);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
    }

    @Operation(summary = "블랙 리스트 이메일 검색",description = "블랙 리스트에서 이메일을 검색하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value =  "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":false,\"blackList\":[{\"id\":1,\"email\":\"user1@example.com\",\"blackCheck\":true},{\"id\":2,\"email\":\"user2@example.com\",\"blackCheck\":true}]}}")))
    })
    @GetMapping("/black/search")
    public ResponseEntity<?> searchEmail(@RequestParam(value = "email",required = false)String email,
                                         @RequestParam(value = "lastId",required = false) Long lastId,
                                         @Parameter(example = "{\"size\":10}") Pageable pageable){
        BlackListResponse blackListResponse = blackMemberService.searchEmailBlackList(email, lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",blackListResponse));
    }
}
