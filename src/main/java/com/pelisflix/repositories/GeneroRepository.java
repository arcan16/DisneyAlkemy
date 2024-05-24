package com.pelisflix.repositories;

import com.pelisflix.models.GeneroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneroRepository extends JpaRepository<GeneroEntity, Long> {
     /*Esta consulta permite la busqueda individual de cada elemento de la lista
     poporcionada como parametro dentro de la base de datos y generar una lista con
     los registros que coinciden*/
    List<GeneroEntity> findByNombreIn(List<String> genero);
}
