package com.bluewhaletech.Ourry.config;

import com.bluewhaletech.Ourry.jwt.JwtAccessDeniedHandler;
import com.bluewhaletech.Ourry.jwt.JwtAuthenticationEntryPoint;
import com.bluewhaletech.Ourry.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter authenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAccessDeniedHandler accessDeniedHandler, JwtAuthenticationFilter authenticationFilter, JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationFilter = authenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        /* CSRF(Cross-Site Request Forgery) 비활성화 */
        http.cors(AbstractHttpConfigurer::disable);
        /* CORS(Cross-Origin Resource Sharing) 비활성화 */
        http.csrf(AbstractHttpConfigurer::disable);

        /* HTTP 기본 인증 비활성화 */
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        /* 서버에서 세션을 생성하거나 사용하는 등 관리하지 않도록 설정 */
        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        /* 401 또는 403 오류 핸들링 */
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        /* UsernamePasswordAuthenticationFilter 앞에 JwtAuthenticationFilter 추가 */
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        /* 인증을 요구하지 않는 API 목록 정의 */
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                .requestMatchers("/member/createAccount").permitAll() // 회원가입 API
                .requestMatchers("/member/memberLogin").permitAll() // 로그인 API
                .requestMatchers("/member/sendAuthenticationCode").permitAll() // 이메일 인증코드 발송 API
                .requestMatchers("/member/emailAuthentication").permitAll() // 이메일 인증 API
                .requestMatchers("/member/passwordReset").permitAll() // 비밀번호 재설정 API
                .requestMatchers("/member/reissueToken").permitAll() // JWT 토큰 재발급 API

                .requestMatchers("/article/getQuestionList").permitAll()
                .requestMatchers("/article/getQuestionDetail/{questionId}").permitAll()
                .requestMatchers("/article/addQuestion").permitAll()
                .requestMatchers("/article/answerQuestion").permitAll()
                .anyRequest().authenticated() // 위 URL 목록을 제외한 나머지 요청에 대해 인증 수행
        );
        return http.build();
    }
}