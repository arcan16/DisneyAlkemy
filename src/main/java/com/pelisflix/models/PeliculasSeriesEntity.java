package com.pelisflix.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pelisflix.dto.peliculasSeries.AddPeliculaSerieDTO;
import com.pelisflix.dto.peliculasSeries.UpdatePeliculaSerieDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.List;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity(name = "PeliculasSeriesEntity")
@Table(name = "peliculas_series")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeliculasSeriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    private String imagen;

    @NotNull
    @Size(max = 80)
    @Column(unique = true)
    private String titulo;

    @NotNull
    private Date fechaCreacion;

    @NotNull
    @Min(value = 1, message = "El valor debe ser al menos 1")
    @Max(value = 5, message = "El valor debe ser maximo 5")
    private Integer calificacion;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = PersonajeEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "peliculas_personajes", joinColumns = @JoinColumn(name = "pelicula_id"), inverseJoinColumns = @JoinColumn(name = "personaje_id"))
    @JsonIgnore
    private List<PersonajeEntity> personajes;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = GeneroEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "peliculas_genero", joinColumns = @JoinColumn(name = "pelicula_id"), inverseJoinColumns = @JoinColumn(name = "genero_id"))
    @JsonIgnore
    private List<GeneroEntity> genero;

    public PeliculasSeriesEntity(AddPeliculaSerieDTO addPeliculaSerieDTO, String peliculasSeriesPath){
        this.titulo = addPeliculaSerieDTO.titulo();
        this.calificacion = addPeliculaSerieDTO.calificacion();
        this.fechaCreacion = addPeliculaSerieDTO.fechaCreacion();
        String fileName = addPeliculaSerieDTO.imagen().getOriginalFilename();
        Path path = Paths.get(peliculasSeriesPath + fileName);
        this.imagen = path.toString();
        this.genero = genero;
    }

    public void eliminarAsociacionPersonajes() {
        this.personajes.clear();
    }
}
