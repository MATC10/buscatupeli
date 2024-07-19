package com.matc.buscatupeli.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GenreResponse { //clase para transferencia de datos, para usar la api externa
     @JsonProperty("genres") //serialización y deserialización de objetos
     private List<Genre> genres;
 }