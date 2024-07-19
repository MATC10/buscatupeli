package com.matc.buscatupeli.repositories;

import com.matc.buscatupeli.models.Genre;
import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.models.Trope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TropeRepository extends JpaRepository<Trope, Long> {
        List<Trope> findTropeByGenre(Genre genre);

}
