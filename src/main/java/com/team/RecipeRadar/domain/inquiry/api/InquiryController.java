package com.team.RecipeRadar.domain.inquiry.api;

import com.team.RecipeRadar.domain.inquiry.application.InquiryService;
import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.inquiry.dto.InquiryResponse;
import com.team.RecipeRadar.domain.inquiry.dto.info.UserInfoInquiryResponse;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserAddInquiryDto;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserDeleteInquiryDto;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserUpdateInquiryDto;
import com.team.RecipeRadar.domain.inquiry.exception.InquiryException;
import com.team.RecipeRadar.domain.inquiry.exception.ex.InquiryNotFoundException;
import com.team.RecipeRadar.domain.post.dto.PostResponse;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.exception.ex.PostNotFoundException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "일반 사용자 문의사항 컨트롤러", description = "일반 사용자 문의사항 작업")
})
@Slf4j
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의사항 작성 API", description = "로그인한 사용자만 문의사항 작성 가능", tags = {"일반 사용자 문의사항 컨트롤러"} )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"InquiryTitle\": \"[작성한 문의사항제목]\", \"memberId\": \"[사용자 ID]\", \"inquiryId\": \"[문의사항 ID]\", \"created_at\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/api/user/inquires")
    public ResponseEntity<?> addInquiry(@Valid @RequestBody UserAddInquiryDto userAddInquiryDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }
            Inquiry save = inquiryService.save(userAddInquiryDto);

            UserAddInquiryDto addResponse = new UserAddInquiryDto(
                    save.getInquiryTitle(),
                    save.getMember().getId());

            return ResponseEntity.ok(new ControllerApiResponse(true,"성공", addResponse));
        }catch (NoSuchElementException e){
            throw new InquiryException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "전체 문의사항 조회 API", description = "전체 사용자만 전체를 조회할 수 있습니다.", tags = {"일반 사용자 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InquiryResponse.class)),
                            examples = @ExampleObject(value = "[{\"inquiryId\": \"[문의사항 ID]\", \"inquiryTitle\": \"[문의사항 제목]\", \"memberId\": \"[사용자 ID]\", \"created_at\": \"LocalDateTime\"}]"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/user/inquires")
    public  ResponseEntity<List<InquiryResponse>> findAllInquires() {
        List<InquiryResponse> inquires = inquiryService.findAll()
                .stream()
                .map(InquiryResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(inquires);
    }

    @Operation(summary = "문의사항 상세 조회 API", description = "사용자가 문의사항의 상세 정보를 조회할 수 있습니다.", tags = {"일반 사용자 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = InquiryResponse.class),
                            examples = @ExampleObject(value = "{\"inquiryId\": \"[문의사항 ID]\", \"inquiryTitle\": \"[문의사항 제목]\", \"memberId\": \"[사용자 ID]\", \"created_at\": \"LocalDateTime\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("api/user/inquires/{id}")
    public  ResponseEntity<InquiryResponse> findInquiry(@PathVariable long id) {
        Inquiry inquiry = inquiryService.findById(id);

        return  ResponseEntity.ok()
                .body(new InquiryResponse(inquiry));
    }

    @Operation(summary = "문의사항 삭제 API",description = "로그인한 사용자만 문의사항 삭제가능",tags = {"일반 사용자 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"문의사항 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

    })
    @DeleteMapping("/api/user/inquires/{id}")
    public ResponseEntity<?> deleteInquiry(@RequestBody UserDeleteInquiryDto userDeleteInquiryDto) {
        try{
            inquiryService.delete(userDeleteInquiryDto);
            return ResponseEntity.ok(new ControllerApiResponse(true,"문의사항 삭제 성공"));
        } catch (NoSuchElementException e) {
            throw new InquiryNotFoundException(e.getMessage());
        }
    }

    @Operation(summary = "문의사항 수정 API", description = "로그인, 작성자만 수정가능", tags = {"일반 사용자 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"inquiryTitle\": \"[수정한 문의사항 제목]\", \"memberId\": \"[사용자 ID]\", \"inquiryId\": \"[문의사항 ID]\", \"update_At\": \"[수정 시간]\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/api/user/inquires/{id}")
    public  ResponseEntity<?> updateInquiry(@Valid @RequestBody UserUpdateInquiryDto updateInquiryDto, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }

            inquiryService.update(
                    updateInquiryDto.getMemberId(),
                    updateInquiryDto.getInquiryId(),
                    updateInquiryDto.getInquiryTitle()
            );
            Inquiry inquiry = inquiryService.findById(updateInquiryDto.getInquiryId());
            UserUpdateInquiryDto userUpdateInquiryDto = new UserUpdateInquiryDto(
                    inquiry.getInquiryTitle(),
                    inquiry.getMember().getId(),
                    inquiry.getId()
            );
            return ResponseEntity.ok(new ControllerApiResponse(true,"문의사항 수정 성공", userUpdateInquiryDto));
        }catch (NoSuchElementException e){
            throw new InquiryNotFoundException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }

}
