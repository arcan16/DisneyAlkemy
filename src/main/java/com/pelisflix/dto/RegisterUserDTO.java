package com.pelisflix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterUserDTO(@NotNull String username,
                              @NotNull String password,
                              @NotNull String nombre,
                              @NotNull String apellido,
                              @NotNull @Email String email) {
}
