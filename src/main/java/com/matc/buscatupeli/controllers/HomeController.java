package com.matc.buscatupeli.controllers;

import com.matc.buscatupeli.models.Genre;
import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.services.GenreService;
import com.matc.buscatupeli.services.MovieService;
import com.matc.buscatupeli.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    @Autowired
    private GenreService genreService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Movie> movies = movieService.findLast20ByReleaseDateAndOriginalLanguage();
        model.addAttribute("movies", movies);

        List<Genre> genres = genreService.findAll();
        model.addAttribute("genres", genres);


        return "home";
    }

    //cogemos las letras insertadas en el buscador home y las peliculas encontradas que
    //contengan esas letras aparecen en found_movies
    @GetMapping("/home/search/found")
    public String search(@RequestParam String title, Model model) {
        List<Movie> movies = movieService.findByTitleContains(title);
        model.addAttribute("movies", movies);
        return "found_movies";
    }

    //TODAS LAS PELÍCULAS CON, AL MENOS, TODOS LOS TROPES MARCADOS POR EL USUARIO CON MÁS DE 0 VOTOS
    @PostMapping("/search/tropes/movies")
    public String searchByTropes(@RequestParam List<Long> tropeIds, Model model) {
        Long tropeCount = (long) tropeIds.size();
        List<Movie> movies = movieService.findMoviesByTropes(tropeIds, tropeCount);
        model.addAttribute("movies", movies);
        return "found_movies";
    }

}