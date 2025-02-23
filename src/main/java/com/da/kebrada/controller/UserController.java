package com.da.kebrada.controller;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.dto.LoginDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.repository.UserRepository;
import com.da.kebrada.service.AuthenticationService;
import com.da.kebrada.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserRepository repository;
    private final UserService service;
    private final AuthenticationService authenticationService;

    public UserController(UserRepository repository, UserService service, AuthenticationService authenticationService) {
        this.repository = repository;
        this.service = service;
        this.authenticationService = authenticationService;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserDTO dto) {
        User newUser = service.registerUser(dto);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        String token = authenticationService.authenticate(loginDTO);
        return ResponseEntity.ok(token);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO dto) {
        User updatedUser = service.updateUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();  // Retorna 204 No Content
    }
}
