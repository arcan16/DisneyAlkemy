package com.pelisflix.repositories;

import com.pelisflix.models.PersonajeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonajesRepository extends JpaRepository<PersonajeEntity, Long> {
    List<PersonajeEntity> findAllByNombre(String nombre);

    List<PersonajeEntity> findAllByEdad(Integer age);

    List<PersonajeEntity> findByPeliculasId(Integer idMovie);

    List<PersonajeEntity> findAllByPeso(Float peso);

    boolean existsByNombre(String nombre);

    List<PersonajeEntity> findByIdIn(List<String> personajes);
}
