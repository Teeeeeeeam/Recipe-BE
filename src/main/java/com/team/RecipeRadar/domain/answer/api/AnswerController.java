package com.team.RecipeRadar.domain.answer.api;

import com.team.RecipeRadar.domain.answer.application.AnswerService;
import com.team.RecipeRadar.domain.answer.domain.Answer;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminAddAnswerDto;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminDeleteAnswerDto;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminUpdateAnswerDto;
import com.team.RecipeRadar.domain.answer.exception.AnswerException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "관리자 문의사항 응답 컨트롤러", description = "관리자 문의사항 관련 응답 작업"),
})
@Slf4j
public class AnswerController {
    
    private final AnswerService answerService;

    @Operation(summary = "응답 작성 API", description = "관리자만 응답을 작성 가능", tags = {"관리자 문의사항 응답 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"응답 작성 성공\", \"data\" : {\"answerContent\": \"[작성한 응답]\", \"memberId\": \"[사용자 ID]\", \"inquiryId\": \"[게시글 ID]\", \"created_at\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"회원정보나 게시글을 찾을수 없습니다.\"}]"))),
    })
    @PostMapping("/api/admin/answers")
    public ResponseEntity<?> answerAdd(@Valid @RequestBody AdminAddAnswerDto adminAddAnswerDto, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }
            Answer save = answerService.save(adminAddAnswerDto);

            AdminAddAnswerDto addResponse = new AdminAddAnswerDto(save.getAnswerContent(), save.getMember().getId(),save.getInquiry().getId(),save.getCreated_at());

            return ResponseEntity.ok(new ControllerApiResponse(true,"성공",addResponse));
        }catch (NoSuchElementException e){
            throw new AnswerException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "응답 삭제 API",description = "관리자만 댓글 삭제가능",tags = {"관리자 문의사항 응답 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"댓글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

    })
    @DeleteMapping("/api/admin/answers")
    public ResponseEntity<?> answerDelete(@RequestBody AdminDeleteAnswerDto adminDeleteAnswerDto){
        try{
            answerService.delete_answer(adminDeleteAnswerDto);//반환타입 void
            return ResponseEntity.ok(new ControllerApiResponse(true,"응답 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new AnswerException(e.getMessage());         //예외처리-> 여기서 처리안하고  @ExceptionHandler로 예외처리함
        }
    }

    @Operation(summary = "댓글 수정 API",description = "로그인, 작성자만 수정가능",tags = {"일반 사용자 댓글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"댓글 수정 성공\" , \"data\" : {\"answerContent\": \"[수정한 댓글]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"update_At\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",

                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"수정할 댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"[오류내용]\"}]")))
    })
    @PutMapping("/api/admin/answers")
    public ResponseEntity<?> answerUpdate(@Valid @RequestBody AdminUpdateAnswerDto updateAnswerDto, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,bindingResult.getFieldError().getDefaultMessage()));
            }

            answerService.update(updateAnswerDto.getMemberId(),updateAnswerDto.getAnswerId(),updateAnswerDto.getAnswerContent());
            Answer answer = answerService.findById(updateAnswerDto.getAnswerId());
            AdminUpdateAnswerDto adminUpdateAnswerDto = new AdminUpdateAnswerDto(answer.getAnswerContent(), answer.getMember().getId(), answer.getId(), answer.getUpdated_at());

            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 수정 성공",adminUpdateAnswerDto));
        }catch (NoSuchElementException e){
            throw new AnswerException(e.getMessage());
        }
        catch (AnswerException e){
            throw new AnswerException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }
    
}


