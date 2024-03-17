package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.exception.AlreadyLoggedOutException;
import com.bluewhaletech.Ourry.exception.ErrorCode;
import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
    private static final String TOKEN_TYPE = "Bearer";

    private final JwtProvider tokenProvider;
    private final RedisBlackListManagement redisBlackListManagement;

    @Autowired
    public JwtAuthenticationFilter(JwtProvider tokenProvider, RedisBlackListManagement redisBlackListManagement) {
        this.tokenProvider = tokenProvider;
        this.redisBlackListManagement = redisBlackListManagement;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            /* Access Token 인입여부 및 유효성 확인 */
            String token = request.getHeader(AUTHORIZATION_HEADER);
            if(!StringUtils.hasText(token)) {
                request.setAttribute("exception", ErrorCode.EMPTY_JWT.getCode());
                return;
            } else if(!token.startsWith(TOKEN_TYPE)) {
                request.setAttribute("exception", ErrorCode.JWT_MALFORMED.getCode());
                return;
            }

            /* Access Token 검증 */
            String accessToken = token.substring(7);
            if(tokenProvider.validateAccessToken(accessToken) && doNotLogout(accessToken)) {
                /* 인증(Authentication) 정보를 가져와서 SecurityContext 내부에 저장 */
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch(SecurityException | MalformedJwtException e) {
            request.setAttribute("exception", ErrorCode.JWT_MALFORMED.getCode());
        } catch(ExpiredJwtException e) {
            request.setAttribute("exception", ErrorCode.JWT_EXPIRED.getCode());
        } catch(BadCredentialsException e) {
            request.setAttribute("exception", ErrorCode.BAD_CREDENTIALS.getCode());
        } catch(UnsupportedJwtException e) {
            request.setAttribute("exception", ErrorCode.JWT_UNSUPPORTED.getCode());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private boolean doNotLogout(String accessToken) {
        String status = redisBlackListManagement.checkLogout(accessToken);
        if(status != null && status.equals("LOGOUT")) {
            throw new AlreadyLoggedOutException("이미 로그아웃이 완료된 토큰입니다.");
        }
        return true;
    }
}