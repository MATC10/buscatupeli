package com.matc.buscatupeli.controllers;

import com.matc.buscatupeli.models.*;
import com.matc.buscatupeli.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DetailController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieTropeService movieTropeService;


    @Autowired
    private VoteService voteService;

    @Autowired
    private GenreService genreService;

@GetMapping("/detail/{id}")
public String detail(@PathVariable("id") Long id, Model model) {

//Esto es para enviar la fecha al html de esta forma: dd/MM/yyyy
    Movie movie = movieService.findById(id);
    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
    Date date;
    try {
        date = originalFormat.parse(movie.getReleaseDate());
        movie.setReleaseDate(targetFormat.format(date));
    } catch (ParseException e) {
        e.printStackTrace();
    }
    model.addAttribute("movie", movie);



    //para obtener los genres de cada película
    Movie movieGenres = movieService.findById(id);
    Set<Genre> genres = movieGenres.getGenres();
    List<String> genreNames = new ArrayList<>();
    for (Genre genre : genres) {
        String genreName = genre.getName();
        genreNames.add(genreName);
    }
    model.addAttribute("genreNames", genreNames);




    // Solamente me devuelve los tropes con voteCount mayor que 0
    List<MovieTrope> movieTropes = movieTropeService.findTropesByMovieId(id);
    List<MovieTrope> filteredMovieTropes = movieTropes.stream()
            .filter(trope -> trope.getVoteCount() > 0)
            .collect(Collectors.toList());

    // Ordenar los MovieTropes por voteCount en orden descendente
    filteredMovieTropes.sort(Comparator.comparing(MovieTrope::getVoteCount).reversed());

    model.addAttribute("tropes", filteredMovieTropes);


        /*para que aparezcan todos los tropes existentes salvo los que tengan relación con esa
    película con un vote_count mayor que 0. Es decir, todos los tropes que me
    devuelva tienen un vote count de 0 o menos,
    o pueden no estar relacionados con la película.
    Solamente me devuelve los tropes con voteCount mayor que 0*/
    // MovieTrope con vote_count > 0
    List<MovieTrope> highVoteMovieTropes = movieTropeService.findTropesByMovieId(id).stream()
            .filter(trope -> trope.getVoteCount() > 0)
            .collect(Collectors.toList());

    // extraer los Tropes de los MovieTrope
    List<Trope> highVoteTropes = highVoteMovieTropes.stream()
            .map(MovieTrope::getTrope)
            .collect(Collectors.toList());

    // obtener todos los géneros
    List<Genre> allGenres = genreService.findAll();
    // filtrar los Tropes de cada género
    for (Genre genre : allGenres) {
        genre.setTropes(
                genre.getTropes().stream()
                        .filter(trope -> !highVoteTropes.contains(trope))
                        .collect(Collectors.toList())
        );
    }
    // añadir los géneros al modelo
    model.addAttribute("allGenres", allGenres);
    return "detail";
}


    //votar los clichés que ya están relacionados con las películas
    @PostMapping("/vote")
    public RedirectView voteMovieTrope(@RequestParam int tropeId, @RequestParam int movieId, @RequestParam int voteValue) {
        voteService.voteMovieTrope(tropeId, movieId, voteValue);
        return new RedirectView("redirect:/detail/" + movieId);
    }

    //relacionar un cliché con una película
    @PostMapping("/propose/trope")
    public RedirectView proposeTrope(@RequestParam int tropeId, @RequestParam int movieId) {
        voteService.proposeTrope(tropeId, movieId);
        return new RedirectView("redirect:/detail/" + movieId);
    }

}