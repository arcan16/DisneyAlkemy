package com.pelisflix.controllers;

import com.pelisflix.dto.RegisterUserDTO;
import com.pelisflix.models.UserEntity;
import com.pelisflix.repositories.UserRepository;
import com.pelisflix.services.EmailSendGrid;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSendGrid emailSendGrid;

    /**
     * Crea un registro de usuario con los datos recibidos
     * @param registerUserDTO Record responsable de recibir el json y convertirlo en un objeto para poder
     *                        utilizar su informacion
     * @return Mensaje con informacion sobre el resultado de la consulta
     * @throws IOException Controla las excepciones que podrian surgir de manipular los directorios y archivos
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) throws IOException {
        if(userRepository.existsByUsername(registerUserDTO.username()))
            return ResponseEntity.badRequest().body("{\"err\":\"El usuario ya existe\"}");

        UserEntity user = new UserEntity(registerUserDTO,passwordEncoder.encode(registerUserDTO.password()));
        userRepository.save(user);
        emailSendGrid.mail(user);
        return ResponseEntity.ok().body("{\"message\":\"Registro exitoso\"}");
    }
}
