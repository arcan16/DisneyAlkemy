package com.pelisflix.controllers;

import com.pelisflix.dto.generos.AddGeneroDTO;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.repositories.GeneroRepository;
import com.pelisflix.services.GeneroService;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

@RestController
@RequestMapping("/genero")
public class GeneroController {

    @Autowired
    private GeneroRepository generoRepository;

    @Value("${spring.genero.image}")
    private String generoPath;

    @Autowired
    private GeneroService generoService;

    /**
     * Crea un nuevo registro de genero
     * @param addGeneroDTO Record encargado de desserializar el json resibido en la peticion
     * @return Mensaje con informacion sobre el resultado
     * @throws IOException Controla las excepciones que pudieran surgir de manejar los directorios
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> addGenero(@ModelAttribute AddGeneroDTO addGeneroDTO) throws IOException {
        if(generoService.addGenero(addGeneroDTO))
            return ResponseEntity.ok().body("{\"message\":\"Genero creado correctamente\"}");
        return ResponseEntity.badRequest().body("{\"err\":\"Ocurrio un error al guardar el registro\"}");
    }

    /**
     * Elimina un registro de la base de datos utilizando el parametro recibido en la peticion
     * @param id Clve primaria recibida a traves de la url que se utilizara para la eliminacion del registro
     * @return Mensaje con informacion sobre el resultado de consulta
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGenero(@PathVariable Long id){
        return generoService.deleteGenero(id);
    }
}
