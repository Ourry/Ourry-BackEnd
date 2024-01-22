package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.exception.JwtTokenExpiredException;
import com.bluewhaletech.Ourry.exception.JwtTokenNotFoundException;
import com.bluewhaletech.Ourry.repository.RedisJwtRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "Refresh";
    private static final String TOKEN_TYPE = "Bearer";

    private final JwtProvider tokenProvider;
    private final RedisJwtRepository redisJwtRepository;

    @Autowired
    public JwtAuthenticationFilter(JwtProvider tokenProvider, RedisJwtRepository redisJwtRepository) {
        this.tokenProvider = tokenProvider;
        this.redisJwtRepository = redisJwtRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /* 클라이언트가 보낸 요청으로부터 Access Token 불러오기 */
        String accessToken = resolveToken(request, AUTHORIZATION_HEADER);
        if(accessToken != null) {
            String refreshToken = resolveToken(request, REFRESH_HEADER);
            if(refreshToken == null) {
                /* Access Token 인증이 성공한 경우, 인증(Authentication) 정보를 가져와서 SecurityContext 내부에 저장  */
                if(StringUtils.hasText(accessToken) && tokenProvider.validateAccessToken(accessToken)) {
                    Authentication authentication = tokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                /* Access Token 인증이 실패했거나 만료된 경우, 401 Unauthorized 오류를 발생시켜서 Access Token 재발급 유도 */
                else {
                    throw new JwtTokenExpiredException("Access Token이 만료됐습니다. 토큰을 재발급해주세요.");
                }
            } else {
                /* Refresh Token 유요한 경우, Access Token 재발급 및 인증(Authentication) 정보를 생성해서 SecurityContext 내부에 저장 */
                if(compareRefreshToken(refreshToken)) {
                    Authentication authentication = tokenProvider.getAuthentication(accessToken);
                    JwtDTO token = tokenProvider.createToken(authentication);
                    tokenProvider.setResponseHeader(response, AUTHORIZATION_HEADER, TOKEN_TYPE + " " + token.getAccessToken());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                /* Refresh Token 만료된 경우, JWT 만료 오류를 발생시켜서 로그인 유도 */
                else {
                    throw new JwtTokenExpiredException("Refresh Token이 만료됐습니다. 다시 로그인해주세요.");
                }
            }
        }
        /* Access Token 만료 & Refresh Token 만료 */
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean compareRefreshToken(String token) {
        return redisJwtRepository.existsByRefreshToken(token);
    }
}