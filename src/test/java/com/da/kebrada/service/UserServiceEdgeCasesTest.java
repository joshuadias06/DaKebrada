package com.da.kebrada.service;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceEdgeCasesTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("Jane Doe", "jane@test.com", "98765432100", "998877665", "senha123");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNullOnRegister(){
        UserDTO invalidDTO = new UserDTO("Jane Doe", "jane@test.com", "98765432100", "998877665", null);

        Exception exception = assertThrows(NullPointerException.class,
                () -> userService.registerUser(invalidDTO));

        assertNotNull(exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmptyOnRegister(){
        UserDTO invalidDTO = new UserDTO("Jane Doe", "jane@test.com", "98765432100", "998877665", "");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(invalidDTO));

        assertEquals("Senha não pode estar vazia",exception.getMessage());
    }

    @Test
    void shouldHandleUnexpectedExceptionFromRepositoryGraceFully(){
        when(repository.findByEmail(userDTO.email())).thenThrow(new RuntimeException("Banco caiu"));

        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.findByEmail(userDTO.email()));

        assertEquals("Banco caiu", exception.getMessage());
    }
}
