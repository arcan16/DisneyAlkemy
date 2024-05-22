package com.pelisflix.dto.generos;

import com.pelisflix.models.GeneroEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record GenerosListDTO(Long id,
                             String nombre,
                             byte[] imagen) {
    public GenerosListDTO(GeneroEntity genero){
        this(genero.getId(), genero.getNombre(), getImageBytes(genero.getImagen()));
    }

    private static byte[] getImageBytes(String imgPath){
        try {
            return Files.readAllBytes(Path.of(imgPath));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
