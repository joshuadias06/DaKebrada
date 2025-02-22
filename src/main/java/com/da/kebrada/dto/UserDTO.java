package com.da.kebrada.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank String name,
        @Email String email,
        @NotBlank String cpf,
        @NotBlank String phone,
        @NotBlank String password
) {}
