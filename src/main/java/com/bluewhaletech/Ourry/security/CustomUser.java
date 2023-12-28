package com.bluewhaletech.Ourry.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {
    public CustomUser(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
    }
}