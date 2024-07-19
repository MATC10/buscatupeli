package com.matc.buscatupeli.services;

import com.matc.buscatupeli.models.Genre;
import com.matc.buscatupeli.models.GenreResponse;
import com.matc.buscatupeli.repositories.GenreRepository;
import com.matc.buscatupeli.repositories.TropeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${themoviedb.api.key}")
    private String apiKey;

    @Autowired
    TropeRepository tropeRepository;


    public Genre findById(Long id) {
        return genreRepository.findById(id).orElse(null);
    }

    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    public ArrayList<Genre> findAll() {
        return (ArrayList<Genre>) genreRepository.findAll();
    }


    public void insertGenresIntoDatabase() {
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=es";
        GenreResponse response = restTemplate.getForObject(url, GenreResponse.class);

        if (response != null) {
            for (Genre genre : response.getGenres()) {
                if (!genreRepository.existsById(genre.getId())) { //tenemos en cuenta que no se repitan los géneros
                    genreRepository.save(genre);
                }
            }
        }
    }
}
