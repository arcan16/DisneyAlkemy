package com.pelisflix.controllers;

import com.pelisflix.dto.personajes.AddCharacterDTO;
import com.pelisflix.dto.personajes.AllCharactersDTO;
import com.pelisflix.dto.personajes.CharacterDetails;
import com.pelisflix.dto.personajes.UpdateCharacterDTO;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.GeneroRepository;
import com.pelisflix.repositories.PeliculasSeriesRepository;
import com.pelisflix.repositories.PersonajesRepository;
import com.pelisflix.services.PersonajesService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class PersonajesController {

    @Autowired
    private PersonajesRepository personajesRepository;

    @Value("${spring.character.image}")
    private String characterPath;

    @Autowired
    private PeliculasSeriesRepository peliculasSeriesRepository;

    @Autowired
    private PersonajesService personajesService;

    /**
     * Crea un nuevo registro en la tabla personajes
     * @param addCharacterDTO Modelo de datos encargado de deserializar la informacion y convertirla en un objeto
     * @return Mensaje de confirmacion al registrar con exito al personaje
     * @throws IOException Controla la excepcion que se pudiera generar al crear un directorio
     */
    @PostMapping("/addCharacter")
    @Transactional
    public ResponseEntity<?> addCharacter(@ModelAttribute AddCharacterDTO addCharacterDTO) throws IOException {
        return personajesService.add(addCharacterDTO);
    }

    /**
     * Obtiene los detalles del personaje, incluyendo las peliculas o series en las que participo
     * @param id Identificador recibido en la peticion
     * @return Objeto con la informacion detallada del registro
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<?> characterDetails(@PathVariable @NotNull Long id){
        return personajesService.details(id);
    }

    /**
     * Filtra los personajes de acuerdo al parametro recibido, y en caso de no recibir ninguno
     * listara los personajes de la tabla personajes en paginas con un maximo de 10 elementos
     * @param name Nombre del personaje
     * @param age Edad del personaje
     * @param peso Peso del personaje
     * @param movies Id de la pelicula en la que aparece
     * @param pageable Tamaño de elementos que contedra la respuesta
     * @return
     * @throws IOException
     */
    @GetMapping("/characters")
    public ResponseEntity<?> getCharactersOrFilterByName(@RequestParam(value = "name",required = false) String name,
                                                @RequestParam(value = "age", required = false) Integer age,
                                                @RequestParam(value = "peso", required = false) Float peso,
                                                @RequestParam(value = "movies", required = false) Integer movies,
                                                @PageableDefault(size = 10)Pageable pageable) throws IOException {

        return personajesService.getCharactersFilter(name,age,peso,movies, pageable);
    }

    /**
     * Actualiza los datos de un personajes
     * @param updateCharacterDTO datos recibidos en la peticion
     * @return Mensaje de actualizacion exitosa
     */
    @PutMapping
    @Transactional
    public ResponseEntity<?> updateCharacter(@ModelAttribute UpdateCharacterDTO updateCharacterDTO){
        return personajesService.update(updateCharacterDTO);
    }

    /**
     * Elimina el personaje con el id recibido
     * @param id id de tipo Long del personaje que sera eliminado
     * @return Mensaje de confirmacion con la eliminacion del personaje
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCharacter(@PathVariable Long id){
        return personajesService.delete(id);
    }
}
