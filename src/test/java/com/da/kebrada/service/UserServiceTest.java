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
        //Before Each == Antes de Cada Teste.
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

    @Test
    void shouldFindUserByEmail(){
        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.of(user));

        User foundUser = userService.findByEmail(user.getEmail());

        assertNotNull(foundUser);
        assertEquals("john@test.com", foundUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail(){
        when(repository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findByEmail("notfound@test.com"));

        assertEquals("Usuário não encontrado!", exception.getMessage());
    }

    @Test
    void shouldDeleteUserSuccessfully(){
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        doNothing().when(repository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(user.getEmail()));
        verify(repository, times(1)).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser(){
        when(repository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser("notfound@test.com"));

        assertEquals("Usuário não encontrado!", exception.getMessage());
        verify(repository, never()).delete(any(User.class));
    }

    @Test
    void shouldUpdatedUserSucessfully(){
        User existingUSer = new User("Old Name", "john@test.com","12345678900", "110012938", "encodedPassword");

        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.of(existingUSer));
        when(repository.findByCpfOrPhone(userDTO.cpf(), userDTO.phone())).thenReturn(Optional.of(existingUSer));
        when(passwordEncoder.matches(userDTO.password(), existingUSer.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("newEncodedPassword");
        when(repository.save(any(User.class))).thenReturn(existingUSer);

        User updatedUser = userService.updateUser(userDTO.email(), userDTO);

        assertNotNull(updatedUser);
        assertEquals(userDTO.name(), updatedUser.getName());
        assertEquals("newEncodedPassword", updatedUser.getPassword());
        verify(repository).save(existingUSer);
    }

}
