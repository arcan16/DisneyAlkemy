package com.pelisflix.services;

import com.pelisflix.dto.personajes.AddCharacterDTO;
import com.pelisflix.dto.personajes.AllCharactersDTO;
import com.pelisflix.dto.personajes.CharacterDetails;
import com.pelisflix.dto.personajes.UpdateCharacterDTO;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.PeliculasSeriesRepository;
import com.pelisflix.repositories.PersonajesRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

@Service
public class PersonajesService {
    @Autowired
    private PersonajesRepository personajesRepository;

    @Value("${spring.character.image}")
    private String characterPath;

    @Autowired
    private PeliculasSeriesRepository peliculasSeriesRepository;

    public ResponseEntity<?> add(AddCharacterDTO addCharacterDTO) throws IOException {
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

    public ResponseEntity<?> details(Long id){
        if(!personajesRepository.existsById(id)){
            return ResponseEntity.badRequest().body("{\"err\":\"El personaje indicado no existe\"}");
        }
        return ResponseEntity.ok(personajesRepository.findById(id).map(CharacterDetails::new));
    }

    public ResponseEntity<?> getCharactersFilter(String name, Integer age, Float peso, Integer movies, Pageable pageable){
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

    public ResponseEntity<?> update(UpdateCharacterDTO updateCharacterDTO){
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

    public ResponseEntity<?> delete(Long id){
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
