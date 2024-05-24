package com.pelisflix.services;

import com.pelisflix.dto.peliculasSeries.*;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.GeneroRepository;
import com.pelisflix.repositories.PeliculasSeriesRepository;
import com.pelisflix.repositories.PersonajesRepository;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class PeliculasSeriesService {

    @Value("${spring.peliculas.series.image}")
    private String peliculasSeriesPath;

    @Autowired
    private PeliculasSeriesRepository peliculasSeriesRepository;

    @Autowired
    private PersonajesRepository personajesRepository;

    @Autowired
    private GeneroRepository generoRepository;

    /**
     * Crea un registro con los datos de la pelicula recibidos
     * @param addPeliculaSerieDTO Record responsable de recibir los datos de la peticion y convertirlo a objetos
     * @return Respuesta con codigo de error de acuerdo al resultado del codigo
     * @throws IOException Controla las excepciones que podrian ocacionarse al manejar/crear los directorios
     */
    public ResponseEntity<String> add(AddPeliculaSerieDTO addPeliculaSerieDTO) throws IOException {
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

    /**
     * Actualiza los datos de un registro de PeliculasSeriesEntity
     * @param updatePeliculaSerieDTO Record encargado de desserializar los datos recibidos en formato json
     *                               de la peticion
     * @return Respuesta que indica el resultado de la ejecucion del codigo
     * @throws IOException Controla las posibles excepciones de manejar los directorios
     */
    public ResponseEntity<String> update(UpdatePeliculaSerieDTO updatePeliculaSerieDTO) throws IOException {
        if(!peliculasSeriesRepository.existsById(updatePeliculaSerieDTO.idMovie()))
            return ResponseEntity.badRequest().body("{\"err\":\"La pelicula no existe\"}");
        if(!peliculasSeriesRepository.existsById(updatePeliculaSerieDTO.idMovie()))
            return ResponseEntity.badRequest().body("{\"err\":\"La pelicula indicada no existe\"}");
        PeliculasSeriesEntity peliculasSeries = peliculasSeriesRepository.getReferenceById(updatePeliculaSerieDTO.idMovie());

        if(updatePeliculaSerieDTO.titulo()!=null)
            peliculasSeries.setTitulo(updatePeliculaSerieDTO.titulo());
        if(updatePeliculaSerieDTO.calificacion()!=null)
            peliculasSeries.setCalificacion(updatePeliculaSerieDTO.calificacion());
        if(updatePeliculaSerieDTO.fechaCreacion()!=null)
            peliculasSeries.setFechaCreacion(updatePeliculaSerieDTO.fechaCreacion());
        if(!updatePeliculaSerieDTO.imagen().isEmpty()){
            // Eliminamos la imagen actual
            try {
                File file = new File(String.valueOf(peliculasSeries.getImagen()));
                if(file.exists() && file.delete()){
                    System.out.println("Eliminado correctamente");
                }else {
                    System.out.println("El archivo no fue encontrado");
                }
            }catch (Exception e){
                System.out.println("La imagen no pudo ser eliminada correctamente");
            }
            // Agregamos la imagen nueva
            String fileName = updatePeliculaSerieDTO.imagen().getOriginalFilename();
            Path path = Paths.get(peliculasSeriesPath+fileName);
            Files.createDirectories(path.getParent());
            Files.copy(updatePeliculaSerieDTO.imagen().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            peliculasSeries.setImagen(path.toString());
        }

        if(updatePeliculaSerieDTO.personajes()!=null && !updatePeliculaSerieDTO.personajes().isEmpty()){
            List<PersonajeEntity> personajeList = personajesRepository.findByIdIn(updatePeliculaSerieDTO.personajes());
            peliculasSeries.setPersonajes(personajeList);
        }
        if(updatePeliculaSerieDTO.genero()!=null && !updatePeliculaSerieDTO.genero().isEmpty()){
            List<GeneroEntity> generoEntityList = generoRepository.findByNombreIn(updatePeliculaSerieDTO.genero());
            peliculasSeries.setGenero(generoEntityList);
        }
        peliculasSeriesRepository.save(peliculasSeries);

        return ResponseEntity.ok().body("{\"message\":\"Registro actualizado correctamente\"}");
    }

    /**
     * Consulta en la base de datos los registros que coincidan con el parametro recibido
     * @param nombre Parametro recibido que indica el nombre del personaje incluido en la pelicula
     * @param idGenero Parametro recibodo que indica el genero incluido en la pelicula
     * @param order Orden ascendente o descendente para los registros almacenados en la base de datos
     * @return Resultado de la consulta realizada con los parametros recibidos
     */
    public ResponseEntity<?> getAll(String nombre, Long idGenero, EOrder order){
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

    /**
     * Consulta la informacion completa de un registro de la base de datos
     * @param id Parametro recibido de la base de datos con el cual se realizara la consulta
     * @return Resultado de la consulta realizada
     */
    public ResponseEntity<?> getAllDetails(Long id){
        if(!peliculasSeriesRepository.existsById(id))
            return ResponseEntity.badRequest().body("{\"err\":\"La pelicula no existe\"}");
        return ResponseEntity.ok(peliculasSeriesRepository.findById(id)
                .stream().map(PeliculasSeriesDetailsCompleteDTO::new));
    }

    /**
     * Elimina un registro utilizando el parametro recibido en la peticion
     * @param id Dato de tipo long que representa la llave primaria del registro que se desea eliminar
     * @return Mensaje con el resultado de la eliminacion
     */
    public ResponseEntity<?> delete(Long id){
        if(peliculasSeriesRepository.existsById(id)){
            peliculasSeriesRepository.deleteById(id);
        }else{
            return ResponseEntity.badRequest().body("{\"message\":\"La pelicula indicada no existe\"}");
        }
        return ResponseEntity.ok().body("{\"message\":\"Pelicula Eliminada Correctamente\"}");
    }
}
