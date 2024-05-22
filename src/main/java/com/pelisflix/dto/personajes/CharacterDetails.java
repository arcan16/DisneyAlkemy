package com.pelisflix.dto.personajes;

import com.pelisflix.dto.peliculasSeries.PeliculasSeriesDetailsDTO;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public record CharacterDetails(Long id,
                               Integer edad,
                               String historia,
                               byte[] imagen,
                               String nombre,
                               Float peso,
                               List<PeliculasSeriesDetailsDTO> peliculasSeriesList) {
    public CharacterDetails(PersonajeEntity personaje){
        this(personaje.getId(), personaje.getEdad(), personaje.getHistoria(),
                getImageBytes(personaje.getImagen()), personaje.getNombre(), personaje.getPeso(),
                personaje.getPeliculas().stream().map(PeliculasSeriesDetailsDTO::new).collect(Collectors.toList()));
    }

    private static byte[] getImageBytes(String imgPath){
        try {
            return Files.readAllBytes(Path.of(imgPath));
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
