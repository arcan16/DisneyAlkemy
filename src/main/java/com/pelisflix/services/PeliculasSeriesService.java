package com.pelisflix.services;

import com.pelisflix.dto.peliculasSeries.UpdatePeliculaSerieDTO;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import com.pelisflix.repositories.GeneroRepository;
import com.pelisflix.repositories.PeliculasSeriesRepository;
import com.pelisflix.repositories.PersonajesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class PeliculasSeriesService {

    @Autowired
    private PeliculasSeriesRepository peliculasSeriesRepository;

    @Autowired
    private PersonajesRepository personajesRepository;

    @Autowired
    private GeneroRepository generoRepository;

    public ResponseEntity<String> update(UpdatePeliculaSerieDTO updatePeliculaSerieDTO, String peliculasSeriesPath) throws IOException {
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

}
