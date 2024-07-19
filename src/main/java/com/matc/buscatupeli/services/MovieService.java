package com.matc.buscatupeli.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.matc.buscatupeli.models.Genre;
import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.models.MovieResponse;
import com.matc.buscatupeli.models.Trope;
import com.matc.buscatupeli.repositories.GenreRepository;
import com.matc.buscatupeli.repositories.MovieRepository;
import com.matc.buscatupeli.repositories.TropeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreService genreService;

    @Value("${themoviedb.api.key}")
    private String apiKey;

    @Value("themoviedb.api.token")
    private String apiToken;



    public ArrayList<Movie> findAll() {
        return (ArrayList<Movie>) movieRepository.findAll();
    }

    public Movie findById(long id) {
        return movieRepository.findById(id);
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public void deleteById(long id) {
        movieRepository.deleteById(id);
    }



    public Page<Movie> findAll(Pageable pageable){
        return movieRepository.findAll(pageable);
    }


    public void deleteMovie(Long movieId) {
        movieRepository.deleteMovieGenresByMovieId(movieId);
        movieRepository.deleteMovieTropesByMovieId(movieId);
        movieRepository.deleteById(movieId);
    }

    public List<Movie> findByTitleContains(String title) {
        return movieRepository.findByTitleContains(title);
    }

    public List<Movie> findLast20ByReleaseDateAndOriginalLanguage() {
        return movieRepository.findLast20ByReleaseDateAndOriginalLanguage();
    }

    public List<Movie> findMoviesByTropes(List<Long> tropeIds, Long tropeCount) {
        return movieRepository.findMoviesByTropes(tropeIds, tropeCount);
    }

    //método para traer películas de la API externa
    public void getMoviesFromAPI(String urlOption) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        ExecutorService executor = Executors.newFixedThreadPool(10); // Ajusto el número de hilos

        int resultPage = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            final int page = resultPage;
            //enviar una tarea al ExecutorService con un hilo separado
            Future<MovieResponse> future = executor.submit(() -> { //future es para operaciones asíncronas con hilos, y submit devuelve el objeto future
                try {
                    //se crea un HttpRequest.Builder
                    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                            .header("accept", "application/json")
                            .header("Authorization", "Bearer " + this.apiToken)
                            .method("GET", HttpRequest.BodyPublishers.noBody());

                    if ("option1".equals(urlOption)) {
                        requestBuilder.uri(URI.create("https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=es&page=" + page)); //descubrir películas en español
                    } else if ("option2".equals(urlOption)) {
                        requestBuilder.uri(URI.create("https://api.themoviedb.org/4/discover/movie?with_original_language=es&api_key=" + apiKey + "&language=es-ES&page=" + page)); //descubrir películas con idioma original en español
                    }
                    //se construye la solicitud
                    HttpRequest request = requestBuilder.build();
                    //enviar la solicitud y obtener la respuesta
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    //convertimos la respuesta en un objeto MovieResponse
                    return objectMapper.readValue(response.body(), MovieResponse.class);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            try {
                MovieResponse movieResponse = future.get();
                if (movieResponse == null || movieResponse.getResults() == null || movieResponse.getResults().isEmpty() || page > movieResponse.getTotalPages()) {
                    hasMorePages = false;
                    break;
                }
                //crear una lista para almacenar las películas a guardar
                List<Movie> moviesToSave = new ArrayList<>();
                for (Movie movie : movieResponse.getResults()) {
                    //si la película ya existe en la base de datos, se continúa con la siguiente película
                    if (!movieRepository.findByTitle(movie.getTitle()).isEmpty()) {
                        continue;
                    }

                    List<Genre> genresList = new ArrayList<>();
                    for (Genre genre : movie.getGenres()) {
                        Genre genreFromDb = genreService.findById(genre.getId());
                        //Si el género existe en la base de datos, se añade a la lista de géneros
                        if (genreFromDb != null) {
                            genresList.add(genreFromDb);
                        }
                    }
                    //SConvertir la lista de géneros en un conjunto para eliminar duplicados
                    Set<Genre> genresSet = new HashSet<>(genresList);
                    //establecemos los géneros de la película y guardamos
                    movie.setGenres(genresSet);
                    movieRepository.save(movie);
                }
                movieRepository.saveAll(moviesToSave);

            } catch (Exception e) {
                e.printStackTrace();
            }

            resultPage++;
        }

        executor.shutdown();
    }

}




