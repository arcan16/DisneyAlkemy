package com.pelisflix.repositories;

import com.pelisflix.models.PeliculasSeriesEntity;
import com.pelisflix.models.PersonajeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculasSeriesRepository extends JpaRepository<PeliculasSeriesEntity, Long> {


    List<PeliculasSeriesEntity> findByIdIn(List<Long> integers);

    List<PeliculasSeriesEntity> findByPersonajes(PersonajeEntity personajeEntity);

    List<PeliculasSeriesEntity> findAllByGeneroId(Long idGenero);

    List<PeliculasSeriesEntity> findAllByTitulo(String nombre);
}
