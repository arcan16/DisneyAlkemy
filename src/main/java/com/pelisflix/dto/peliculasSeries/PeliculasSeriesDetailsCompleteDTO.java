package com.pelisflix.dto.peliculasSeries;

import com.pelisflix.dto.generos.GenerosListDTO;
import com.pelisflix.dto.personajes.AllCharactersDTO;
import com.pelisflix.dto.personajes.CharacterDetails;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public record PeliculasSeriesDetailsCompleteDTO(@NotNull String titulo,
                                                @NotNull Date fechaCreacion,
                                                @NotNull Integer calificacion,
                                                @NotNull byte[] imagen,
                                                List<AllCharactersDTO> personajeEntities,
                                                List<GenerosListDTO> generoEntityList) {
    public PeliculasSeriesDetailsCompleteDTO(PeliculasSeriesEntity peliculasSeries){
        this(peliculasSeries.getTitulo(),peliculasSeries.getFechaCreacion(),
                peliculasSeries.getCalificacion(), getImageBytes(peliculasSeries.getImagen()),
                peliculasSeries.getPersonajes().stream().map(AllCharactersDTO::new).collect(Collectors.toList()),
                peliculasSeries.getGenero().stream().map(GenerosListDTO::new).collect(Collectors.toList()));
    }

    private static byte[] getImageBytes(String imgPath){
        try {
            return Files.readAllBytes(Path.of(imgPath));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
