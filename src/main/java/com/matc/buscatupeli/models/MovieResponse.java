package com.matc.buscatupeli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) //esto es para ignorar si hay alguna propiedad desconocida en el json que deserializo de la api
public class MovieResponse {
    @JsonProperty("results")
    private List<Movie> results;

    @JsonProperty("total_pages")
    private long totalPages;

    @JsonProperty("page")
    private int page;

    @JsonProperty("total_results")
    private long totalResults;

    @JsonProperty("errors")
    private List<String> errors;

    @JsonProperty("success")
    private boolean success;
}