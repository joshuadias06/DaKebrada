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

import java.util.List;
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

    @Test
    void shouldNotUpdateUserIfCpfOrPhoneBelongsToAnotherUser(){
        User existingUser = new User("Old Name", "john@test.com", "12345678900", "110012938", "encodedPassword");
        User anotherUser = new User("Other User", "other@test.com", "12345678900", "110012938", "encodedPassword");
        anotherUser.setId(999L);

        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.of(existingUser));
        when(repository.findByCpfOrPhone(userDTO.cpf(), userDTO.phone())).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.updateUser(userDTO.email(), userDTO));

        assertEquals("CPF ou telefone já cadastrado por outro usuario!", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUserWithoutChangingPasswordIfMatches() {
        User existingUser = spy(new User("Old Name", "john@test.com", "12345678900", "110012938", "encodedPassword"));

        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.of(existingUser));
        when(repository.findByCpfOrPhone(userDTO.cpf(), userDTO.phone())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(userDTO.password(), existingUser.getPassword())).thenReturn(true);
        when(repository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userDTO.email(), userDTO);

        assertNotNull(updatedUser);
        verify(existingUser, never()).setPassword(any());
        verify(repository).save(existingUser);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser(){
        when(repository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser("notfound@test.com", userDTO));

        assertEquals("Usuário não encontrado!", exception.getMessage());
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(
                new User("User1", "u1@test.com", "123", "111", "pw"),
                new User("User2", "u2@test.com", "456", "222", "pw")
        );

        when(repository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getName());
        assertEquals("User2", result.get(2).getName());
        verify(repository).findAll();
    }


}
