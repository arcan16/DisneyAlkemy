package com.pelisflix.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pelisflix.dto.peliculasSeries.PeliculasSeriesDetailsDTO;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.PeliculasSeriesRepository;
import com.pelisflix.repositories.PersonajesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PeliculasSeriesControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PersonajesRepository personajesRepository;

    @MockBean
    private PeliculasSeriesRepository peliculasSeriesRepository;

    private PersonajeEntity personaje;
    private PeliculasSeriesEntity peliculasSeries;

    @BeforeEach
    void setUp() {
        GeneroEntity genero = new GeneroEntity();
        genero.setId(1L);
        genero.setNombre("Test");
        genero.setImagen("src\\main\\resources\\images\\characters\\ToyStory.webp");

        peliculasSeries = new PeliculasSeriesEntity();
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
    @WithMockUser
    @DisplayName("Test - Agregar pelicula o serie")
    void addMovie() throws Exception {
        String expected = "[{\"campo\":\"titulo\",\"error\":\"must not be null\"}," +
                "{\"campo\":\"calificacion\",\"error\":\"must not be null\"}," +
                "{\"campo\":\"fechaCreacion\",\"error\":\"must not be null\"}," +
                "{\"campo\":\"imagen\",\"error\":\"must not be null\"}]";

        mvc.perform(multipart("/addMovieSerie"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expected));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Consulta utilizando parametros como filtro")
    void getAllMovies() throws Exception {
        List<PeliculasSeriesEntity> peliculasSeriesList = new ArrayList<>();
        peliculasSeriesList.add(peliculasSeries);

        List<PeliculasSeriesDetailsDTO> peliculasSeriesDetailsDTOS = peliculasSeriesList.stream().map(PeliculasSeriesDetailsDTO::new).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        String expectedJson = objectMapper.writeValueAsString(peliculasSeriesDetailsDTOS);

        Sort sort = Sort.by("fechaCreacion").ascending();

        when(peliculasSeriesRepository.findAllByTitulo(peliculasSeries.getTitulo())).thenReturn(peliculasSeriesList);
        when(peliculasSeriesRepository.findAllByGeneroId(1L)).thenReturn(peliculasSeriesList);
        when(peliculasSeriesRepository.findAll(sort)).thenReturn(peliculasSeriesList);

        mvc.perform(get("/movies")
                        .param("nombre",peliculasSeries.getTitulo()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        mvc.perform(get("/movies")
                        .param("idGenero", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        mvc.perform(get("/movies")
                        .param("order", "ASC"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Consulta de pelicula o serie usando id")
    void getAllMoviesDetails() throws Exception {
        mvc.perform(get("/moviesDetails/{id}", 6))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"err\":\"La pelicula no existe\"}"));
        mvc.perform(get("/moviesDetails/{id}", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"err\":\"Tipo de dato incorrecto\"}"));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Actualizacion de datos")
    void updateMovie() throws Exception {
        String expectedResponse = "[{\"campo\":\"idMovie\",\"error\":\"must not be null\"}]";
        String expectedResponse2 = "[{\"campo\":\"idMovie\",\"error\":\"Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\\"a\\\"\"}]";

        mvc.perform(put("/updateMovieSerie"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponse));

        mvc.perform(put("/updateMovieSerie")
                        .param("idMovie", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponse2));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Eliminacion de registro ")
    void deleteMovieSerie() throws Exception {
        when(peliculasSeriesRepository.existsById(1L)).thenReturn(false);
        mvc.perform(delete("/deleteMovieSerie/{id}",6))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"La pelicula indicada no existe\"}"));

//        when(peliculasSeriesRepository.existsById(1L)).thenReturn(false);
        mvc.perform(delete("/deleteMovieSerie/{id}","a"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"err\":\"Tipo de dato incorrecto\"}"));

    }
}