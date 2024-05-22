package com.pelisflix.controllers;

import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    private PersonajeEntity personaje;

    @BeforeEach
    void setUp() {
        GeneroEntity genero = new GeneroEntity();
        genero.setId(1L);
        genero.setNombre("Test");
        genero.setImagen("src\\main\\resources\\images\\characters\\ToyStory.webp");

        PeliculasSeriesEntity peliculasSeries = new PeliculasSeriesEntity();
        peliculasSeries.setId(1L);
        peliculasSeries.setTitulo("test");
        peliculasSeries.setGenero(List.of(genero));
        peliculasSeries.setCalificacion(5);
        peliculasSeries.setImagen("src\\main\\resources\\images\\characters\\ToyStory.webp");
        peliculasSeries.setFechaCreacion(Date.valueOf(LocalDate.of(2024, 5, 18)));

        personaje = new PersonajeEntity();
        personaje.setId(1L);
        personaje.setNombre("Omar");
        personaje.setEdad(32);
        personaje.setImagen("src\\main\\resources\\images\\characters\\ToyStory.webp");
        personaje.setHistoria("Vendedor que programa");
        personaje.setPeso(76);
        personaje.setPeliculas(List.of(peliculasSeries));
    }

    @Test
    @DisplayName("Test sobre usuario duplicado")
    void registrarUsuarioDuplicado() throws Exception {

        when(userRepository.existsByUsername("Omar")).thenReturn(true);

        mvc.perform(post("/auth/register")
                        .param("username","oma")
                        .param("password","test")
                        .param("nombre","omar")
                        .param("apellido","test")
                        .param("email", "email@test.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test Parametros incompletos")
    void registrarUsuarioParametrosIncompletos() throws Exception {
        String expectedResponse ="[{\"campo\":\"apellido\",\"error\":\"must not be null\"}," +
                "{\"campo\":\"password\",\"error\":\"must not be null\"}," +
                "{\"campo\":\"username\",\"error\":\"must not be null\"}," +
                "{\"campo\":\"nombre\",\"error\":\"must not be null\"}," +
                "{\"campo\":\"email\",\"error\":\"must not be null\"}]";

        when(userRepository.existsByUsername("Omar")).thenReturn(true);

        mvc.perform(post("/auth/register")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponse));

    }
}