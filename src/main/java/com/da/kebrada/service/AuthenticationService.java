package com.da.kebrada.service;

import com.da.kebrada.repository.UserRepository;
import com.da.kebrada.security.JwtService;
import com.da.kebrada.dto.LoginDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(UserRepository repository, JwtService jwtService,
                                 AuthenticationManager authManager, UserDetailsService userDetailsService) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
    }

    public String authenticate(LoginDTO loginDTO) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password())
        );

        UserDetails user = userDetailsService.loadUserByUsername(loginDTO.email());
        return jwtService.generateToken(user.getUsername());
    }
}
