package com.bluewhaletech.Ourry.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecurityException e) {
            log.error("잘못된 JWT 서명입니다.");
            setErrorResponse(response, "잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 토큰입니다.");
            setErrorResponse(response, "잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰이 만료됐습니다. 토큰을 재발급해주세요.");
            setErrorResponse(response, "JWT 토큰이 만료됐습니다. 토큰을 재발급해주세요.");
        } catch (BadCredentialsException | UnsupportedJwtException e) {
            log.error("유효하지 않거나 지원되지 않는 JWT 토큰입니다.");
            setErrorResponse(response, "유효하지 않거나 지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("올바르지 않는 JWT 토큰 형식입니다.");
            setErrorResponse(response, "올바르지 않는 JWT 토큰 형식입니다.");
        } catch (JwtException e) {
            log.error(e.getMessage());
            setErrorResponse(response, e.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
        JSONObject object = new JSONObject();
        object.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        object.put("message", message);

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(object);
    }
}