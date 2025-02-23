package com.da.kebrada.service;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repository) {
        this.repository = repository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(UserDTO dto) {
        User user = new User(
                dto.name(),
                dto.email(),
                dto.cpf(),
                dto.phone(),
                passwordEncoder.encode(dto.password())
        );

        return repository.save(user);
    }

    public User updateUser(Long id, UserDTO dto) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado!");
        }

        User user = userOptional.get();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setCpf(dto.cpf());
        user.setPhone(dto.phone());
        user.setPassword(passwordEncoder.encode(dto.password()));

        return repository.save(user);
    }

    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado!");
        }
        repository.deleteById(id);
    }
}