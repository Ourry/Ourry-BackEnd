package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.dto.AuthenticationDTO;
import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.exception.AuthorizationNotFoundException;
import com.bluewhaletech.Ourry.security.CustomUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

@Component
public class JwtProvider {
    private final SecretKey SECRET_KEY;
    private final Long ACCESS_TOKEN_EXPIRATION;
    private final Long REFRESH_TOKEN_EXPIRATION;

    @Autowired
    public JwtProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.atk}") Long accessTokenExpiration, @Value("${jwt.rtk}") Long refreshTokenExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
        this.ACCESS_TOKEN_EXPIRATION = accessTokenExpiration;
        this.REFRESH_TOKEN_EXPIRATION = refreshTokenExpiration;
    }

    public JwtDTO createToken(AuthenticationDTO dto) {
        /* 오늘 날짜를 시간으로 변경해서 가져옴 */
        long now = new Date(System.currentTimeMillis()).getTime();

        /* 인증(Authentication) 정보로부터 권한 목록 불러오기 */
        String authorities = dto.getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        /* Access Token 생성 */
        String accessToken = Jwts.builder()
                .subject(dto.getTokenName())
                .claim("Authorization", authorities)
                .issuedAt(new Date(now))
                .expiration(new Date(now + ACCESS_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();

        /* Refresh Token 생성 */
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(dto.getTokenId()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + REFRESH_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();

        return JwtDTO.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(ACCESS_TOKEN_EXPIRATION)
                .refreshTokenExpiration(REFRESH_TOKEN_EXPIRATION)
                .build();
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
    }

    public Long getTokenId(String refreshToken) {
        return Long.parseLong(getClaims(refreshToken).getPayload().getSubject());
    }

    public String getTokenSubject(String accessToken) {
        return getClaims(accessToken).getPayload().getSubject();
    }

    public Long getTokenExpiration(String token) {
        long now = new Date().getTime();
        return getClaims(token).getPayload().getExpiration().getTime()-now;
    }

    /* Access Token 복호화를 통한 인증(Authentication) 정보 가져오기 */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token).getPayload();
        if(claims.get("Authorization") == null) {
            throw new AuthorizationNotFoundException("권한 정보가 존재하지 않는 토큰입니다");
        }

        /* 권한(Authority) 정보 가져오기 */
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("Authorization").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        /* CustomUser 객체를 만들어서 인증(Authentication) 정보 반환 */
        CustomUser principal = new CustomUser(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /* Access Token 유효성 체크 */
    public boolean validateAccessToken(String token) {
        Date expiredDate = getClaims(token).getPayload().getExpiration();
        return expiredDate.after(new Date());
    }
}