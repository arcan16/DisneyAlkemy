package com.pelisflix.repositories;

import com.pelisflix.models.GeneroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneroRepository extends JpaRepository<GeneroEntity, Long> {
    List<GeneroEntity> findByNombreIn(List<String> genero);

    List<GeneroEntity> findAllByIdIn(List<String> genero);
}
