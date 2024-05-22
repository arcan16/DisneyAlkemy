package com.pelisflix.dto.peliculasSeries;

import com.pelisflix.models.GeneroEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

public record AddPeliculaSerieDTO(@NotNull String titulo,
                                  @NotNull Date fechaCreacion,
                                  @NotNull Integer calificacion,
                                  @NotNull MultipartFile imagen,
                                  List<String> genero,
                                  List<String> personajes) {
}
