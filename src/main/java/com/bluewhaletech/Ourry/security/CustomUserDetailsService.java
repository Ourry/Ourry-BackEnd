package com.bluewhaletech.Ourry.security;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.exception.MemberNotFoundException;
import com.bluewhaletech.Ourry.repository.MemberJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final MemberJpaRepository jpaMemberRepository;

    @Autowired
    public CustomUserDetailsService(PasswordEncoder passwordEncoder, MemberJpaRepository jpaMemberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jpaMemberRepository = jpaMemberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = Optional.ofNullable(jpaMemberRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        return CustomUser.builder()
                .username(email)
                .password(passwordEncoder.encode(member.getPassword()))
                .roles(member.getRole().toString())
                .build();
    }
}