package com.matc.buscatupeli.controllers;

import com.matc.buscatupeli.models.Genre;
import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.services.GenreService;
import com.matc.buscatupeli.services.MovieService;
import com.matc.buscatupeli.services.TropeService;
import com.matc.buscatupeli.util.paginator.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/crud/movies")
public class CrudMoviesController {

    @Autowired
    MovieService movieService;

    @Autowired
    TropeService tropeService;

    @Autowired
    GenreService genreService;

    //lista de tods las películas
    @GetMapping("/list")
    public String listMovies(Model model){
        model.addAttribute("listMovies", movieService.findAll());
        return "crud_movie";
    }

    //añadir nueva película nueva
    @GetMapping("/add")
    public String addMovie(Model model){
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreService.findAll());
        return "formulario";
    }


    //guarda la nueva película en la bbdd
    @PostMapping("/postadd")
    public String postAddMovie(@ModelAttribute("movie") Movie movie, @RequestParam List<Long> genres){
        for(Long genreId : genres){
            Genre genre = genreService.findById(genreId);
            movie.getGenres().add(genre);
        }
        movieService.save(movie);
        return "redirect:/crud/movies/add";
    }

    //para actualizar la película existente, se añade la película y los géneros seleccionados
    // a la película y redirige al formulario
    @GetMapping("/update/{id}")
    public String updateMovie(Model model, @PathVariable long id){
        Movie movie = movieService.findById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreService.findAll());
        return "formulario";
    }

    //para modificar la película, se borran los géneros y se añaden de nuevo,
    // finalmente se guarda la peli y se redirige al crud de películas.
    @PostMapping("/postupdate")
    public String postUpdateMovie(@ModelAttribute("movie") Movie movie, @RequestParam List<Long> genres){
        movie.getGenres().clear();
        for(Long genreId : genres){
            Genre genre = genreService.findById(genreId);
            movie.getGenres().add(genre);
        }
        movieService.save(movie);
        return "redirect:/crud/movies";
    }

    //borrar película por id
    @GetMapping("/delete/{id}")
    public String deleteMovies(@PathVariable long id, Model model){
        movieService.deleteMovie(id);
        return "redirect:/crud/movies";
    }


    //lista las películas con paginación, también se formatean las fechas con otro método que hay más abajo
    @GetMapping({""})
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        Pageable pageRequest = PageRequest.of(page, 10);
        Page<Movie> movies = movieService.findAll(pageRequest);
        PageRender<Movie> pageRender = new PageRender<>("", movies);

        model.addAttribute("page", pageRender);
        model.addAttribute("listMovies", formatMovieDates(movies.getContent()));
        model.addAttribute("movies", movies);

        return "crud_movie";
    }


    //Método que se le agrega al controlador "listar" para formatear la fecha
    private List<Movie> formatMovieDates(List<Movie> movies) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
        return movies.stream().map(movie -> {
            try {
                Date date = originalFormat.parse(movie.getReleaseDate());
                movie.setReleaseDate(targetFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return movie;
        }).collect(Collectors.toList());
    }

    //método para importar nuevas películas desde la API externa
    @GetMapping("/getMoviesFromAPI/novedades")
    public String getMoviesFromAPI() {
        try {
            movieService.getMoviesFromAPI("option1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return  "redirect:/crud/movies"; // Asegúrate de devolver la vista correcta
    }

    //método para importar nuevas películas en lenguaje original castellano desde la API externa
    @GetMapping("/getMoviesFromAPI/spanish")
    public String getSpanishMoviesFromAPI() {
        try {
            movieService.getMoviesFromAPI("option2");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return  "redirect:/crud/movies"; // Asegúrate de devolver la vista correcta
    }

}
