package com.pelisflix.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pelisflix.dto.personajes.AddCharacterDTO;
import com.pelisflix.dto.personajes.UpdateCharacterDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Entity(name = "PersonajeEntity")
@Table(name = "personajes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "peliculas")
public class PersonajeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String imagen;

    @NotBlank
    @Size(max = 80)
    @Column(unique = true)
    private String nombre;

    @NotNull
    private Integer edad;

    @NotNull
    private float peso;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String historia;

    @ManyToMany(mappedBy = "personajes")
    @JsonBackReference
    private List<PeliculasSeriesEntity> peliculas;

    public PersonajeEntity(AddCharacterDTO addCharacterDTO, Path path) {
        this.imagen = path.toString();
        this.nombre = addCharacterDTO.nombre();
        this.edad = addCharacterDTO.edad();
        this.peso = addCharacterDTO.peso();
        this.historia = addCharacterDTO.historia();
    }

    public void update(UpdateCharacterDTO updateCharacterDTO, String characterPath) {
        if(updateCharacterDTO.nombre()!=null)
            this.nombre = updateCharacterDTO.nombre();

        if(updateCharacterDTO.edad()!=null)
            this.edad = updateCharacterDTO.edad();

        if(updateCharacterDTO.peso()!=null)
            this.peso = updateCharacterDTO.peso();

        if(updateCharacterDTO.historia()!=null)
            this.historia = updateCharacterDTO.historia();
        if(updateCharacterDTO.imagen()!=null){
            String fileName = updateCharacterDTO.imagen().getOriginalFilename();
            Path path = Paths.get(characterPath + fileName);
            this.imagen = path.toString();
        }
    }
}
