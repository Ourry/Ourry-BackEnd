package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtProvider tokenProvider;
    private final RedisBlackListManagement redisBlackListManagement;

    @Autowired
    public JwtAuthenticationFilter(JwtProvider tokenProvider, RedisBlackListManagement redisBlackListManagement) {
        this.tokenProvider = tokenProvider;
        this.redisBlackListManagement = redisBlackListManagement;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = tokenProvider.resolveToken(request, AUTHORIZATION_HEADER);
        /* Access Token 인증이 성공한 경우 */
        if(StringUtils.hasText(accessToken) && doNotLogout(accessToken) && tokenProvider.validateAccessToken(accessToken)) {
            /* 인증(Authentication) 정보를 가져와서 SecurityContext 내부에 저장 */
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private boolean doNotLogout(String accessToken) {
        String status = redisBlackListManagement.checkLogout(accessToken);
        if(status.equals("LOGOUT")) {
            throw new JwtException("로그아웃이 완료된 토큰입니다.");
        }
        return true;
    }
}