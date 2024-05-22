package com.pelisflix.models;

import com.pelisflix.dto.RegisterUserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "UserEntity")
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotNull
    @Size(max = 255)
    private String password;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @NotNull
    @Size(max = 50)
    private String apellido;

    @NotNull
    @Size(max = 50)
    @Email
    private String email;

    public UserEntity(RegisterUserDTO registerUserDTO, String password) {
        this.username = registerUserDTO.username();
        this.password = password;
        this.nombre = registerUserDTO.nombre();
        this.apellido = registerUserDTO.apellido();
        this.email = registerUserDTO.email();
    }
}
