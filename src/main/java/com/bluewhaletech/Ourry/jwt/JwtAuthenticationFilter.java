package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.RefreshToken;
import com.bluewhaletech.Ourry.exception.MemberNotFoundException;
import com.bluewhaletech.Ourry.repository.MemberRepository;
import com.bluewhaletech.Ourry.repository.RedisJwtRepository;
import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "Refresh";

    private final JwtProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RedisJwtRepository redisJwtRepository;
    private final RedisBlackListManagement redisBlackListManagement;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public JwtAuthenticationFilter(JwtProvider tokenProvider, MemberRepository memberRepository, RedisJwtRepository redisJwtRepository, RedisBlackListManagement redisBlackListManagement, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
        this.redisJwtRepository = redisJwtRepository;
        this.redisBlackListManagement = redisBlackListManagement;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = tokenProvider.resolveToken(request, AUTHORIZATION_HEADER);
        if(accessToken != null) {
            String refreshToken = tokenProvider.resolveToken(request, REFRESH_HEADER);
            /* 로그인 이후에 인증이 필요한 요청을 보내는 경우 */
            if(refreshToken == null) {
                /* Access Token 인증이 성공한 경우 */
                if(StringUtils.hasText(accessToken) && doNotLogout(accessToken) && tokenProvider.validateAccessToken(accessToken)) {
                    /* 인증(Authentication) 정보를 가져와서 SecurityContext 내부에 저장 */
                    Authentication authentication = tokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            /* Access Token 재발급이 필요한 경우 */
            else {
                /* 로그아웃 요청을 보내면 Access Token 검증 */
                if(request.getRequestURI().equals("/member/memberLogout")) {
                    tokenProvider.validateAccessToken(accessToken);
                }
                /* Refresh Token 유효한 경우 */
                else if(validateRefreshToken(refreshToken)) {
                    /* Refresh Token으로부터 사용자(Member) 정보 가져오기 */
                    Member member = memberRepository.findOne(tokenProvider.getTokenId(refreshToken))
                            .orElseThrow(() -> new MemberNotFoundException("Refresh Token으로 사용자를 조회할 수 없습니다."));
                    /* 인증(Authentication) 정보를 생성해서 SecurityContext 내부에 저장 */
                    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                            new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean doNotLogout(String accessToken) {
        String status = redisBlackListManagement.checkLogout(accessToken);
        if(status != null && status.equals("LOGOUT")) {
            throw new JwtException("로그아웃이 완료된 토큰입니다. 다시 확인해주세요.");
        }
        return true;
    }

    private boolean validateRefreshToken(String refreshToken) {
        Long tokenId = tokenProvider.getTokenId(refreshToken);
        RefreshToken storedRefreshToken = redisJwtRepository.findById(tokenId)
                .orElseThrow(() -> new JwtException("토큰이 만료됐거나 유효하지 않습니다."));
        return refreshToken.equals(storedRefreshToken.getTokenValue());
    }
}