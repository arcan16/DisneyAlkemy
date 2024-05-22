package com.pelisflix.dto.peliculasSeries;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

public record UpdatePeliculaSerieDTO(@NotNull Long idMovie,
                                     String titulo,
                                     Date fechaCreacion,
                                     Integer calificacion,
                                     MultipartFile imagen,
                                     List<String> genero,
                                     List<String> personajes) {
}
