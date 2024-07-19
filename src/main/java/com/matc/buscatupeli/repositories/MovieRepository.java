package com.matc.buscatupeli.repositories;

import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.models.Trope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAll();
    public Movie findById(long id);
    public Movie save(Movie movie);
    List<Movie> findByTitle(String title);

    Page<Movie> findAll(Pageable pageable);

    @Modifying //etiqueta para informar que se va a eliminar algo de la bbdd
    @Transactional
    @Query(value = "DELETE FROM movie_genres WHERE movie_id = :movieId", nativeQuery = true)
    void deleteMovieGenresByMovieId(Long movieId);

    @Modifying //etiqueta para informar que se va a eliminar algo de la bbdd
    @Transactional
    @Query(value = "DELETE FROM movie_trope WHERE movie_id = :movieId", nativeQuery = true)
    void deleteMovieTropesByMovieId(Long movieId);


    List<Movie> findByTitleContains(@Param("title") String title);

    //para poder usar LIMIT tengo que poner nativeQuery = true
    @Query(value = "SELECT * FROM Movie m WHERE m.release_date BETWEEN '2023-02-08' AND '2024-05-31' AND (m.original_language = 'en' OR m.original_language = 'es') AND m.overview IS NOT NULL AND m.overview <> '' AND m.backdrop_path IS NOT NULL ORDER BY m.release_date DESC LIMIT 20", nativeQuery = true)
    List<Movie> findLast20ByReleaseDateAndOriginalLanguage();

    //ESTA CONSULTA NOS DEVUELVE TODAS LAS PELÍCULAS CON, AL MENOS, TODOS LOS TROPES MARCADOS POR EL USUARIO CON MÁS DE 0 VOTOS
    @Query("SELECT m FROM Movie m WHERE :tropeCount = (SELECT COUNT(mt) FROM MovieTrope mt WHERE mt.trope.id IN :tropeIds AND mt.movie = m AND mt.voteCount > 0)")
    List<Movie> findMoviesByTropes(@Param("tropeIds") List<Long> tropeIds, @Param("tropeCount") Long tropeCount);
}

