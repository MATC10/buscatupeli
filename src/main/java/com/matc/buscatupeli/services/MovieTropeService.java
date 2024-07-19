package com.matc.buscatupeli.services;

import com.matc.buscatupeli.models.MovieTrope;
import com.matc.buscatupeli.repositories.MovieTropeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieTropeService {

    @Autowired
    MovieTropeRepository movieTropeRepository;

    public List<MovieTrope> findAll() {
        return movieTropeRepository.findAll();
    }

    public MovieTrope findById(Long id) {
        return movieTropeRepository.findById(id).orElse(null);
    }

    public MovieTrope save(MovieTrope movieTrope) {
        return movieTropeRepository.save(movieTrope);
    }

    public void deleteById(Long id) {
        movieTropeRepository.deleteById(id);
    }
    public List<MovieTrope> findTropesByMovieId(Long movieId) {
        return movieTropeRepository.findTropesByMovieId(movieId);
    }

}