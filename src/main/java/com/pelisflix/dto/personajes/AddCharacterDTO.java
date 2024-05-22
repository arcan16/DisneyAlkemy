package com.pelisflix.dto.personajes;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record AddCharacterDTO(String nombre,
                              Integer edad,
                              float peso,
                              String historia,
                              MultipartFile imagen,
                              List<Long> peliculasSeries
                              ) {
}
