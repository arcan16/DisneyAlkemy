package com.pelisflix.dto.personajes;

import com.pelisflix.models.PersonajeEntity;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record AllCharactersDTO( byte [] imagen, String nombre) {
    public AllCharactersDTO(PersonajeEntity personaje){
        this(getImageBytes(personaje.getImagen()), personaje.getNombre());
    }
    private static byte[] getImageBytes(String imgPath){
        try {
            return Files.readAllBytes(Path.of(imgPath));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
