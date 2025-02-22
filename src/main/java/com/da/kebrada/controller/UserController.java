package com.da.kebrada.controller;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.dto.LoginDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.service.AuthenticationService;
import com.da.kebrada.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService service;
    private final AuthenticationService authenticationService;

    public UserController(UserService service, AuthenticationService authenticationService) {
        this.service = service;
        this.authenticationService = authenticationService;
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
}
