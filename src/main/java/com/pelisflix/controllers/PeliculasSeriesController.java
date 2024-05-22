package com.pelisflix.controllers;

import com.pelisflix.dto.peliculasSeries.*;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.GeneroRepository;
import com.pelisflix.repositories.PeliculasSeriesRepository;
import com.pelisflix.repositories.PersonajesRepository;
import com.pelisflix.services.PeliculasSeriesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PeliculasSeriesController {

    @Autowired
    private PeliculasSeriesRepository peliculasSeriesRepository;

    @Value("${spring.peliculas.series.image}")
    private String peliculasSeriesPath;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private PersonajesRepository personajesRepository;

    @Autowired
    private PeliculasSeriesService peliculasSeriesService;

    @PostMapping("/addMovieSerie")
    public ResponseEntity<?> addMovie(@ModelAttribute @Valid AddPeliculaSerieDTO addPeliculaSerieDTO) throws IOException {

        // Manejo del directorio y la imagen relacionada con la pelicula
        String fileName = addPeliculaSerieDTO.imagen().getOriginalFilename();
        Path path = Paths.get(peliculasSeriesPath + fileName);
        Files.createDirectories(path.getParent());
        Files.copy(addPeliculaSerieDTO.imagen().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        PeliculasSeriesEntity peliculasSeries =
                new PeliculasSeriesEntity(addPeliculaSerieDTO, peliculasSeriesPath);

        // Si recibimos los generos, los agregaremos al registro
        if(!addPeliculaSerieDTO.genero().isEmpty()){
            List<GeneroEntity> generoEntityList = generoRepository.findByNombreIn(addPeliculaSerieDTO.genero());
            peliculasSeries.setGenero(generoEntityList);
        }
        // Si recibimos los personajes, los agregamos al registro
        if(addPeliculaSerieDTO.personajes()!=null){
            List<PersonajeEntity> personajesList = personajesRepository.findByIdIn(addPeliculaSerieDTO.personajes());
            peliculasSeries.setPersonajes(personajesList);
        }

        try {
            peliculasSeriesRepository.save(peliculasSeries);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("{\"err\":\"Ocurrio un error al guardar el registro\"}");
        }

        return ResponseEntity.ok().body("{\"message\":\"Pelicula agregada correctamente\"}");
    }

    @GetMapping("/movies")
    public ResponseEntity<?> getAllMovies(@RequestParam(value = "nombre", required = false)String nombre,
                                          @RequestParam(value = "idGenero", required = false) Long idGenero,
                                          @RequestParam(value = "order", required = false) EOrder order,
                                          @PageableDefault(size = 10)Pageable pageable){

        List<PeliculasSeriesEntity> peliculasSeriesList = new ArrayList<>();
        if(nombre!=null)
            peliculasSeriesList = peliculasSeriesRepository.findAllByTitulo(nombre);
        if(idGenero!=null){
            peliculasSeriesList = peliculasSeriesRepository.findAllByGeneroId(idGenero);
        }
        if(order!=null){
            Sort sort = order.equals(EOrder.ASC)
                    ? Sort.by("fechaCreacion").ascending()
                    : Sort.by("fechaCreacion").descending();
            peliculasSeriesList = peliculasSeriesRepository.findAll(sort);
        }
        if(peliculasSeriesList.isEmpty()){
            return ResponseEntity.ok().body(peliculasSeriesRepository.findAll().stream().map(PeliculasSeriesDetailsDTO::new));
        }
        return ResponseEntity.ok(peliculasSeriesList.stream().map(PeliculasSeriesDetailsDTO::new));
    }

    @GetMapping("/moviesDetails/{id}")
    public ResponseEntity<?> getAllMoviesDetails(@PathVariable @NotNull Long id){
        if(!peliculasSeriesRepository.existsById(id))
            return ResponseEntity.badRequest().body("{\"err\":\"La pelicula no existe\"}");
        return ResponseEntity.ok(peliculasSeriesRepository.findById(id)
                .stream().map(PeliculasSeriesDetailsCompleteDTO::new));
    }

    @PutMapping("/updateMovieSerie")
    public ResponseEntity<?> updateMovie(@ModelAttribute @Valid UpdatePeliculaSerieDTO updatePeliculaSerieDTO) throws IOException {
        if(!peliculasSeriesRepository.existsById(updatePeliculaSerieDTO.idMovie()))
            return ResponseEntity.badRequest().body("{\"err\":\"La pelicula no existe\"}");
        return peliculasSeriesService.update(updatePeliculaSerieDTO, peliculasSeriesPath);
    }

    @DeleteMapping("/deleteMovieSerie/{id}")
    @Transactional
    public ResponseEntity<?> deleteMovieSerie(@PathVariable Long id){
        if(peliculasSeriesRepository.existsById(id)){
            peliculasSeriesRepository.deleteById(id);
        }else{
            return ResponseEntity.badRequest().body("{\"message\":\"La pelicula indicada no existe\"}");
        }
        return ResponseEntity.ok().body("{\"message\":\"Pelicula Eliminada Correctamente\"}");
    }
}
