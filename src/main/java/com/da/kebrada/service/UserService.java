package com.da.kebrada.service;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
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
    public User updateUser(Long id, UserDTO dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));

        validateUserUniquenessForUpdate(user, dto.cpf(), dto.phone());

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setCpf(dto.cpf());
        user.setPhone(dto.phone());

        // Atualiza a senha apenas se for diferente
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        return repository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }
        repository.deleteById(id);
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
