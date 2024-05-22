package com.pelisflix.dto.peliculasSeries;

import com.pelisflix.models.PeliculasSeriesEntity;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;

public record PeliculasSeriesDetailsDTO(@NotNull String titulo,
                                        @NotNull Date fechaCreacion,
                                        @NotNull byte[] imagen) {
    public PeliculasSeriesDetailsDTO (PeliculasSeriesEntity peliculasSeries){
        this(peliculasSeries.getTitulo(),peliculasSeries.getFechaCreacion(),
                getImageBytes(peliculasSeries.getImagen()));
    }

    private static byte[] getImageBytes(String imgPath){
        try {
            return Files.readAllBytes(Path.of(imgPath));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
