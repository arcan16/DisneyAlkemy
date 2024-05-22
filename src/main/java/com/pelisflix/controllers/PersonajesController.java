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

    /**
     * Crea un nuevo registro en la tabla personajes
     * @param addCharacterDTO Modelo de datos encargado de deserializar la informacion y convertirla en un objeto
     * @return Mensaje de confirmacion al registrar con exito al personaje
     * @throws IOException Controla la excepcion que se pudiera generar al crear un directorio
     */
    @PostMapping("/addCharacter")
    @Transactional
    public ResponseEntity<?> addCharacter(@ModelAttribute AddCharacterDTO addCharacterDTO) throws IOException {
        Path path = Paths.get(characterPath);
        if(personajesRepository.existsByNombre(addCharacterDTO.nombre())){
            return ResponseEntity.badRequest().body("{\"err\":\"El personaje ya existe\"}");
        }
        PersonajeEntity personajeEntity = new PersonajeEntity();
        if(addCharacterDTO.imagen()!=null){
            String fileName = addCharacterDTO.imagen().getOriginalFilename();
            path = Paths.get(characterPath+fileName);
            Files.createDirectories(path.getParent());
            Files.copy(addCharacterDTO.imagen().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            personajeEntity = new PersonajeEntity(addCharacterDTO, path);
        }else{

            personajeEntity = new PersonajeEntity(addCharacterDTO,path);
        }

        try {
            personajesRepository.save(personajeEntity);

            if(!addCharacterDTO.peliculasSeries().isEmpty()){
                List<PeliculasSeriesEntity> peliculasSeriesList =
                        peliculasSeriesRepository.findByIdIn(addCharacterDTO.peliculasSeries());

                PersonajeEntity finalPersonajeEntity = personajeEntity;
                peliculasSeriesList.forEach(pelicula->{
                    List<PersonajeEntity> listaPersonaje = new ArrayList<>();
                    listaPersonaje.add(finalPersonajeEntity);
                    listaPersonaje.addAll(pelicula.getPersonajes());
                    pelicula.setPersonajes(listaPersonaje);
                    peliculasSeriesRepository.save(pelicula);
                });
            }
        }catch (Exception e){
            File file = new File(String.valueOf(path.toFile()));
            if(file.exists() && file.delete()){
                System.out.println("Archivo eliminado");
            }
            System.out.println("Error "+ Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok().body("{\"message\":\"Personaje agregado correctamente\"}");
    }




    /**
     * Obtiene los detalles del personaje, incluyendo las peliculas o series en las que participo
     * @param id Identificador recibido en la peticion
     * @return Objeto con la informacion detallada del registro
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<?> characterDetails(@PathVariable @NotNull Long id){
        if(!personajesRepository.existsById(id)){
            return ResponseEntity.badRequest().body("{\"err\":\"El personaje indicado no existe\"}");
        }
        return ResponseEntity.ok(personajesRepository.findById(id).map(CharacterDetails::new));
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

        List<PersonajeEntity> listPersonaje = new ArrayList<>();

        if(name != null){
            listPersonaje = personajesRepository.findAllByNombre(name);
        }

        if(age !=null){
            listPersonaje = personajesRepository.findAllByEdad(age);
        }

        if(peso != null){
            listPersonaje = personajesRepository.findAllByPeso(peso);
        }

        if(movies!=null){
            listPersonaje = personajesRepository.findByPeliculasId(movies);
        }

        if(listPersonaje.isEmpty()){
            return ResponseEntity.ok(personajesRepository.findAll(pageable).stream().map(AllCharactersDTO::new));
        }

        return ResponseEntity.ok().body(listPersonaje.stream().map(CharacterDetails::new));
    }

    /**
     * Actualiza los datos de un personajes
     * @param updateCharacterDTO datos recibidos en la peticion
     * @return Mensaje de actualizacion exitosa
     */
    @PutMapping
    @Transactional
    public ResponseEntity<?> updateCharacter(@ModelAttribute UpdateCharacterDTO updateCharacterDTO){

        Optional<PersonajeEntity> personaje = personajesRepository.findById(updateCharacterDTO.id());

        if(personaje.isEmpty())
            return ResponseEntity.badRequest().body("{\"err\":\"El registro indicado no existe, favor de verificar id\"}");

        personaje.get().update(updateCharacterDTO, characterPath);

        List<PeliculasSeriesEntity> peliculasSeriesUpdate =
                peliculasSeriesRepository.findByIdIn(updateCharacterDTO.peliculasSeries());

        List<PeliculasSeriesEntity> peliculasSeriesActual =
                peliculasSeriesRepository.findByPersonajes(personaje.get());

        for(PeliculasSeriesEntity pelicula : peliculasSeriesActual){
            pelicula.getPersonajes().remove(personaje.get());
            peliculasSeriesRepository.save(pelicula);
        }

        for(PeliculasSeriesEntity peliculaNueva : peliculasSeriesUpdate){
            peliculaNueva.getPersonajes().add(personaje.get());
            peliculasSeriesRepository.save(peliculaNueva);
        }

        personajesRepository.save(personaje.get());

        return ResponseEntity.ok().body("{\"message\":\"Registro actualizado correctamente\"}");
    }

    /**
     * Elimina el personaje con el id recibido
     * @param id id de tipo Long del personaje que sera eliminado
     * @return Mensaje de confirmacion con la eliminacion del personaje
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCharacter(@PathVariable Long id){
        Optional<PersonajeEntity> personaje = personajesRepository.findById(id);

        if(personaje.isEmpty())
            return ResponseEntity.badRequest().body("{\"err\":\"El personaje con id: " +id+" no existe\"}");

        /*
        El siguiente for puede ser evitado si modificamos la constraint de la tabla
        peliculas_personajes:
        ALTER TABLE peliculas_personajes DROP CONSTRAINT Nombre_constraint_pelicula_personaje
        ADD CONSTRAINT Nombre_constraint_pelicula_personaje FOREIGN KEY (`personaje_id`)
        REFERENCES `personajes` (`id`) ON DELETE CASCADE
        El motivo es que hibernate/jpa no tiene soporte para la creacion de ON DELETE CASCADE en relaciones
        @ManyToMany
         */
        for(PeliculasSeriesEntity peliculasSeries : personaje.get().getPeliculas()){
            peliculasSeries.eliminarAsociacionPersonajes();
            peliculasSeriesRepository.save(peliculasSeries);
        }
        personajesRepository.deleteById(id);
        try {
            File file = new File(String.valueOf(personaje.get().getImagen()));
            if(file.exists() && file.delete()){
                System.out.println("Eliminado correctamente");
            }else {
                throw new ValidationException("El registro fue eliminado correctamente, pero la imagen no se encontro");
            }
        }catch (Exception e){
            System.out.println("Error "+ e);
            return ResponseEntity.ok().body("{\"message\":\"El personaje fue eliminado correctamente, sin embargo la imagen no fue encontrada\"}");
        }
        return ResponseEntity.ok().body("{\"message\":\"El personaje fue eliminado correctamente\"}");
    }
}
