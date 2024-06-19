package com.team.RecipeRadar.global.jwt.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Security 예외처리 필터
 */
public class SecurityExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        }catch (JwtTokenException e){
            setErrorResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
        }catch (AccessDeniedException e){
            setErrorResponse(response,e.getMessage(),HttpStatus.UNAUTHORIZED);
        }catch (BadCredentialsException e){
            setErrorResponse(response,e.getMessage(),HttpStatus.UNAUTHORIZED);
        }
    }

    private void setErrorResponse(HttpServletResponse response, String message, HttpStatus unauthorized){

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            response.setCharacterEncoding("UTF-8");
            response.setStatus(unauthorized.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ControllerApiResponse errorResponse = new ControllerApiResponse(false, message);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
