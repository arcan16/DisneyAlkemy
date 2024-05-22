package com.pelisflix.repositories;

import com.pelisflix.models.GeneroEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GeneroRepositoryTest {

    @Mock
    private GeneroRepository generoRepository;

    @Test
    @DisplayName("Prueba para agregar un genero")
    public void test1(){

        MockMultipartFile mockFile = new MockMultipartFile("imagen", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "TEST".getBytes());
        String nombre = "test";

        when(generoRepository.save(any(GeneroEntity.class))).thenReturn(new GeneroEntity());

        assertThat(generoRepository.save(any(GeneroEntity.class))).isNull();

    }
}