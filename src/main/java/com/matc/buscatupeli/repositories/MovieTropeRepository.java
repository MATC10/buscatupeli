package com.matc.buscatupeli.repositories;

import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.models.MovieTrope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MovieTropeRepository extends JpaRepository<MovieTrope, Long> {
    List<MovieTrope> findTropesByMovieId(Long movieId);



}