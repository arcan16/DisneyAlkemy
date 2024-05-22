package com.pelisflix.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pelisflix.dto.generos.AddGeneroDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Entity(name = "GeneroEntity")
@Table(name = "generos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "peliculas")
public class GeneroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(unique = true)
    private String nombre;

    @NotNull
    @Size(max = 255)
    private String imagen;

    @ManyToMany(mappedBy = "genero")
    @JsonBackReference
    private List<PeliculasSeriesEntity> peliculas;

    public GeneroEntity(AddGeneroDTO addGeneroDTO, String generoPath) {
        this.nombre = addGeneroDTO.nombre();
        String fileName = addGeneroDTO.imagen().getOriginalFilename();
        Path pathFile = Paths.get(generoPath + fileName);
        this.imagen = pathFile.toString();
    }
}
