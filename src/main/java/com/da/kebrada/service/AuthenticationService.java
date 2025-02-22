package com.da.kebrada.service;

import com.da.kebrada.repository.UserRepository;
import com.da.kebrada.security.JwtService;
import com.da.kebrada.dto.LoginDTO;
import com.da.kebrada.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.BadCredentialsException;
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
        // Tentando autenticar o usuário
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password", e);  // Exceção caso a autenticação falhe
        }

        // Carregando os detalhes do usuário e gerando o JWT
        UserDetails user = userDetailsService.loadUserByUsername(loginDTO.email());

        // Gerando o token JWT usando o serviço de JWT
        return jwtService.generateToken(user.getUsername());
    }
}
