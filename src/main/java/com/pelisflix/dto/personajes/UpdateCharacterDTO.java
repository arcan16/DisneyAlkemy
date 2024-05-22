package com.pelisflix.dto.personajes;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateCharacterDTO(@NotNull Long id,
                                 String nombre,
                                 Integer edad,
                                 Float peso,
                                 String historia,
                                 MultipartFile imagen,
                                 List<Long> peliculasSeries) {
}
