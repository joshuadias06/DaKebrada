package com.da.kebrada.service;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repository) {
        this.repository = repository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(UserDTO dto) {
        User user = new User(null, dto.name(), dto.email(), dto.cpf(), dto.phone(),
                passwordEncoder.encode(dto.password()));
        return repository.save(user);
    }
}