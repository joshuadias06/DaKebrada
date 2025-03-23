package com.da.kebrada.service;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Transactional
    public User registerUser(UserDTO dto) {
        validateUserUniqueness(dto.cpf(), dto.phone());

        User user = new User(
                dto.name(),
                dto.email(),
                dto.cpf(),
                dto.phone(),
                passwordEncoder.encode(dto.password())
        );

        return repository.save(user);
    }

    @Transactional
    public User updateUser(String email, UserDTO dto) {
        User user = findByEmail(email);

        validateUserUniquenessForUpdate(user, dto.cpf(), dto.phone());

        user.setName(dto.name());
        user.setPhone(dto.phone());

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        return repository.save(user);
    }

    @Transactional
    public void deleteUser(String email) {
        User user = findByEmail(email);
        repository.delete(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));
    }

    private void validateUserUniqueness(String cpf, String phone) {
        if (repository.findByCpfOrPhone(cpf, phone).isPresent()) {
            throw new IllegalArgumentException("CPF ou telefone já cadastrado!");
        }
    }

    private void validateUserUniquenessForUpdate(User user, String newCpf, String newPhone) {
        repository.findByCpfOrPhone(newCpf, newPhone).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(user.getId())) {
                throw new IllegalArgumentException("CPF ou telefone já cadastrado por outro usuário!");
            }
        });
    }
}