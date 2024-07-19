package com.matc.buscatupeli.controllers;

import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.models.MovieTrope;
import com.matc.buscatupeli.models.Trope;
import com.matc.buscatupeli.services.MovieService;
import com.matc.buscatupeli.services.TropeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private MovieService movieService;

    //usado en crud_movie
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam("title") String title) {
        return movieService.findByTitleContains(title);
    }
}