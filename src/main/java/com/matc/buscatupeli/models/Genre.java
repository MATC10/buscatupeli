package com.matc.buscatupeli.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity
public class Genre {
    @Id
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @OneToMany(mappedBy = "genre")
    @ToString.Exclude //el lombok no usa el tostring con este atributo
    @JsonBackReference //cuando jackson convierte el objeto genre a json, no incluye los tropes para evitar una recursión infinita
    private List<Trope> tropes;

    public Genre() {
    }

    public Genre(long id) {
        this.id = id;
    }
}