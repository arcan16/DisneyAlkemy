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
    PersonajeEntity findByNombre(String nombre);

    List<PersonajeEntity> findAllByNombre(String nombre);

    List<PersonajeEntity> findAllByEdad(Integer age);

    List<PersonajeEntity> findByPeliculasId(Integer idMovie);

    List<PersonajeEntity> findAllByPeso(Float peso);

    List<PersonajeEntity> findByNombreIn(List<String> personajes);

    boolean existsByNombre(String nombre);

    @Query("SELECT p FROM PersonajeEntity p WHERE LOWER(p.nombre) = LOWER(?1)")
    Page<PersonajeEntity> findAllByNombre(String name, Pageable pageable);

    Page<PersonajeEntity> findAllByEdad(Integer age, Pageable pageable);

    Page<PersonajeEntity> findAllByPeso(Float peso, Pageable pageable);

    Page<PersonajeEntity> findByPeliculasId(Integer idMovie, Pageable pageable);

    List<PersonajeEntity> findByIdIn(List<String> personajes);
}
