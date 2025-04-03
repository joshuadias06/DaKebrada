package com.da.kebrada.service;

import com.da.kebrada.dto.UserDTO;
import com.da.kebrada.model.User;
import com.da.kebrada.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp(){
        user = new User("John Doe", "john@test.com", "12345678900", "110012938", "encodedPassword");
        userDTO = new UserDTO("John Doe", "john@test.com", "12345678900", "110012938", "encodedPassword");
    }

    @Test
    void shouldRegisterUserSuccessfully(){
        //should == deve; when == quando; thenReturn == então retorne.
        when(repository.findByCpfOrPhone(userDTO.cpf(), userDTO.phone())). thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.registerUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("John Doe", createdUser.getName());
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void shouldNotRegisterUserIfCpfOrPhoneExists(){
        //should == deve; when == quando; thenReturn == então retorne.
        when(repository.findByCpfOrPhone(userDTO.cpf(), userDTO.phone())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(userDTO));

        assertEquals("CPF ou telefone já cadastrado!", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }


}
