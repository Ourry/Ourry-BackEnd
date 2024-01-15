package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.security.CustomUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_TYPE = "bearer";

    private final SecretKey secretKey;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;

    @Autowired
    public JwtProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.atk}") Long accessTokenExpiration, @Value("${jwt.rtk}") Long refreshTokenExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public JwtDTO createToken(Authentication authentication) {
        long now = new Date().getTime();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        /* Access Token 생성 */
        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        /* Refresh Token 생성 */
        String refreshToken = Jwts.builder()
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        return JwtDTO.builder()
                .type(TOKEN_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(new Date(now + accessTokenExpiration).getTime())
                .build();
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    /* 토큰 복호화를 통한 인증(Authentication) 정보 가져오기 */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token).getPayload();
        if(claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 존재하지 않는 토큰입니다");
        }

        /* 권한(Authority) 정보 가져오기 */
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        /* CustomUser 객체를 만들어서 인증(Authentication) 정보 반환 */
        CustomUser principal = new CustomUser(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /* 토큰 Payload 부분에서 email 값 가져오기 */
    public String getEmail(String token) {
        return getClaims(token).getPayload().getSubject();
    }

    /* 토큰 만료여부 확인 */
    public boolean checkExpiration(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.info(e.getMessage());
            return false;
        }
    }

    /* 토큰 유효성 체크 */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("올바르지 않는 JWT 토큰 형식입니다.");
        }
        return false;
    }
}