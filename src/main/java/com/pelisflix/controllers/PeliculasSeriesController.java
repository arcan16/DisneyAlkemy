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
    private PersonajesRepository personajesRepository;

    @Autowired
    private PeliculasSeriesService peliculasSeriesService;

    /**
     * Crea un registro con los datos de la pelicula recibidos
     * @param addPeliculaSerieDTO Record responsable de recibir los datos de la peticion y convertirlo a objetos
     * @return Respuesta con codigo de error de acuerdo al resultado del codigo
     * @throws IOException Controla las excepciones que podrian ocacionarse al manejar/crear los directorios
     */
    @PostMapping("/addMovieSerie")
    public ResponseEntity<?> addMovie(@ModelAttribute @Valid AddPeliculaSerieDTO addPeliculaSerieDTO) throws IOException {
        return peliculasSeriesService.add(addPeliculaSerieDTO);
    }

    /**
     * Consulta en la base de datos los registros que coincidan con el parametro recibido
     * @param nombre Parametro recibido que indica el nombre del personaje incluido en la pelicula
     * @param idGenero Parametro recibodo que indica el genero incluido en la pelicula
     * @param order Orden ascendente o descendente para los registros almacenados en la base de datos
     * @return Resultado de la consulta realizada con los parametros recibidos
     */
    @GetMapping("/movies")
    public ResponseEntity<?> getAllMovies(@RequestParam(value = "nombre", required = false)String nombre,
                                          @RequestParam(value = "idGenero", required = false) Long idGenero,
                                          @RequestParam(value = "order", required = false) EOrder order,
                                          @PageableDefault(size = 10)Pageable pageable){
        return peliculasSeriesService.getAll(nombre,idGenero,order);
    }

    /**
     * Consulta la informacion completa de un registro de la base de datos
     * @param id Parametro recibido de la base de datos con el cual se realizara la consulta
     * @return Resultado de la consulta realizada
     */
    @GetMapping("/moviesDetails/{id}")
    public ResponseEntity<?> getAllMoviesDetails(@PathVariable @NotNull Long id){
        return peliculasSeriesService.getAllDetails(id);
    }

    /**
     * Actualiza los datos de un registro de PeliculasSeriesEntity
     * @param updatePeliculaSerieDTO Record encargado de desserializar los datos recibidos en formato json
     *                               de la peticion
     * @return Respuesta que indica el resultado de la ejecucion del codigo
     * @throws IOException Controla las posibles excepciones de manejar los directorios
     */
    @PutMapping("/updateMovieSerie")
    public ResponseEntity<?> updateMovie(@ModelAttribute @Valid UpdatePeliculaSerieDTO updatePeliculaSerieDTO) throws IOException {
        return peliculasSeriesService.update(updatePeliculaSerieDTO);
    }

    /**
     * Elimina un registro utilizando el parametro recibido en la peticion
     * @param id Dato de tipo long que representa la llave primaria del registro que se desea eliminar
     * @return Mensaje con el resultado de la eliminacion
     */
    @DeleteMapping("/deleteMovieSerie/{id}")
    @Transactional
    public ResponseEntity<?> deleteMovieSerie(@PathVariable Long id){
        return peliculasSeriesService.delete(id);
    }
}
