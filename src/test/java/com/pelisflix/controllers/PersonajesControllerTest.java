package com.pelisflix.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pelisflix.dto.personajes.CharacterDetails;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.PersonajesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@AutoConfigureJsonTesters
@ActiveProfiles("test")
class PersonajesControllerTest {

    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private CharacterDetails characterDetails;

    @MockBean
    private PersonajesRepository personajesRepository;

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
//        peliculasSeries.setFechaCreacion(new Date(System.currentTimeMillis()));

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
    @DisplayName("Test - Consulta de datos del personaje")
    void characterDetails() throws Exception {
        Long personajeId = 1L;

        when(personajesRepository.existsById(personajeId)).thenReturn(true);
        when(personajesRepository.findById(personajeId)).thenReturn(Optional.of(personaje));

        mvc.perform(get("/details/{id}", personajeId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":" + personajeId + "}"));
    }
    @Test
    @WithMockUser
    @DisplayName("Test - Consulta de personaje que no existe")
    void notExistCharacterDetails() throws Exception {
        Long personajeId = 1L;

        when(personajesRepository.existsById(personajeId)).thenReturn(false);

        mvc.perform(get("/details/{id}", personajeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"err\":\"El personaje indicado no existe\"}"));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Creacion de registro exitosa")
    void addCharacter() throws Exception {
        Path filePath = Paths.get("src/main/resources/images/characters/ToyStory.webp");
        InputStream inputStream = Files.newInputStream(filePath);
        MockMultipartFile imagen = new MockMultipartFile("ToyStory.webp",inputStream);

        when(personajesRepository.existsByNombre("omar")).thenReturn(false);
        when(personajesRepository.save(personaje)).thenReturn(new PersonajeEntity());

        mvc.perform(multipart("/addCharacter")
                .file(imagen)
                        .param("nombre", personaje.getNombre())
                        .param("edad", String.valueOf(personaje.getEdad()))
                        .param("peso", String.valueOf(personaje.getPeso()))
                        .param("peliculasSeries", "1")
                ).andExpect(status().isOk()).andExpect((content().json("{\"message\":\"Personaje agregado correctamente\"}")));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Error al crear personaje")
    void addCharacterAlreadyAdded() throws Exception {
        Path filePath = Paths.get("src/main/resources/images/characters/ToyStory.webp");
        InputStream inputStream = Files.newInputStream(filePath);
        MockMultipartFile imagen = new MockMultipartFile("ToyStory.webp",inputStream);

        when(personajesRepository.existsByNombre(personaje.getNombre())).thenReturn(true);
        mvc.perform(multipart("/addCharacter")
                .file(imagen)
                .param("nombre", personaje.getNombre())
                .param("edad", String.valueOf(personaje.getEdad()))
                .param("peso", String.valueOf(personaje.getPeso()))
                .param("peliculasSeries", "1")
        )
                .andExpect(status().isBadRequest())
                .andExpect((content().json("{\"err\":\"El personaje ya existe\"}")));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Consulta de registro usando filtros")
    void getCharactersOrFilterByName() throws Exception {
        List<PersonajeEntity> listPersonajes = new ArrayList<>();
        listPersonajes.add(personaje);

        List<CharacterDetails> characterDetailsList = listPersonajes.stream().map(CharacterDetails::new).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        String expectedJson = objectMapper.writeValueAsString(characterDetailsList);

        when(personajesRepository.findAllByNombre(personaje.getNombre())).thenReturn(listPersonajes);
        when(personajesRepository.findAllByEdad(personaje.getEdad())).thenReturn(listPersonajes);
        when(personajesRepository.findAllByPeso(personaje.getPeso())).thenReturn(listPersonajes);
        when(personajesRepository.findByPeliculasId(1)).thenReturn(listPersonajes);

        // Act & Assert
        mvc.perform(get("/characters")
                        .param("name", personaje.getNombre())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
        mvc.perform(get("/characters")
                        .param("age", String.valueOf(personaje.getEdad()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
        mvc.perform(get("/characters")
                        .param("peso", String.valueOf(personaje.getPeso()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
        mvc.perform(get("/characters")
                        .param("movies", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Actualizacion de datos de un registro")
    void updateCharacter() throws Exception {
        Optional<PersonajeEntity> emptyOptional = Optional.empty();
        when(personajesRepository.findById(personaje.getId())).thenReturn(emptyOptional);
        mvc.perform(put("/")
                .param("id", String.valueOf(1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Test - Eliminacion de registro sin exito")
    void deleteCharacter() throws Exception {
        Optional<PersonajeEntity> emptyOptional = Optional.empty();
        when(personajesRepository.findById(1L)).thenReturn(emptyOptional);
        mvc.perform(delete("/{id}",1L)
                        .param("id", String.valueOf(1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"err\":\"El personaje con id: " +1L+" no existe\"}"));
    }
}
