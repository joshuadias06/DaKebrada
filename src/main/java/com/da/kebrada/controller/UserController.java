package com.da.kebrada.controller;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.dto.LoginDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.service.AuthenticationService;
import com.da.kebrada.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService service;
    private final AuthenticationService authenticationService;

    public UserController(UserService service, AuthenticationService authenticationService) {
        this.service = service;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserDTO dto) {
        return ResponseEntity.ok(service.registerUser(dto));
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        String token = authenticationService.authenticate(loginDTO);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody @Valid UserDTO dto) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User updatedUser = service.updateUser(userDetails.getUsername(), dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        service.deleteUser(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = service.findByEmail(userDetails.getUsername());

        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("name", user.getName());
        userResponse.put("email", user.getEmail());
        userResponse.put("cpf", user.getCpf());
        userResponse.put("phone", user.getPhone());

        return ResponseEntity.ok(userResponse);
    }
}
