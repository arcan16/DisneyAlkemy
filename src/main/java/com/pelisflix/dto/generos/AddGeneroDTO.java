package com.pelisflix.dto.generos;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

public record AddGeneroDTO(@NotNull String nombre,
                           @NotNull MultipartFile imagen) {
}
