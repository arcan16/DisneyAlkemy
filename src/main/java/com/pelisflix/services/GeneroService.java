package com.pelisflix.services;

import com.pelisflix.dto.generos.AddGeneroDTO;
import com.pelisflix.models.GeneroEntity;
import com.pelisflix.repositories.GeneroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

@Service
public class GeneroService {
    @Autowired
    private GeneroRepository generoRepository;

    @Value("${spring.genero.image}")
    private String generoPath;

    public boolean addGenero(AddGeneroDTO addGeneroDTO) throws IOException {
        GeneroEntity genero = new GeneroEntity(addGeneroDTO, generoPath);

        String fileName = addGeneroDTO.imagen().getOriginalFilename();
        Path path = Paths.get(generoPath + fileName);
        Files.createDirectories(path.getParent());
        Files.copy(addGeneroDTO.imagen().getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

        try {
            generoRepository.save(genero);
        }catch (Exception e){
            File file = new File(String.valueOf(path.toFile()));
            if(file.exists() && file.delete()){
                System.out.println("Archivo eliminado");
            }
            System.out.println("Error " + Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }

    public ResponseEntity<String> deleteGenero(Long id){
        if(generoRepository.existsById(id)){
            GeneroEntity genero = generoRepository.getReferenceById(id);
            generoRepository.deleteById(id);
            File file = new File(genero.getImagen());
            if(file.exists() && file.delete()){
                return ResponseEntity.ok().body("{\"message\":\"Genero eliminado correctamente\"}");
            }else{
                return ResponseEntity.ok().body("{\"message\":\"Registro eliminado correctamente, pero la imagen no se encontro\"}");
            }
        }
        else
            return ResponseEntity.ok().body("{\"message\":\"El genero indicadao no existe\"}");
    }
}
