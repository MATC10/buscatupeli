package com.matc.buscatupeli.services;

import com.matc.buscatupeli.models.Genre;
import com.matc.buscatupeli.models.Movie;
import com.matc.buscatupeli.models.Trope;
import com.matc.buscatupeli.repositories.TropeRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TropeService {

    private final TropeRepository tropeRepository;

    public TropeService(TropeRepository tropeRepository) {
        this.tropeRepository = tropeRepository;
    }

    public ArrayList<Trope> findAll() {
        return (ArrayList<Trope>) tropeRepository.findAll();
    }

    public Trope findById(long id) {
        return tropeRepository.findById(id).orElse(null);
    }

    public Trope save(Trope trope) {
        return tropeRepository.save(trope);
    }

    public void deleteById(long id) {
        tropeRepository.deleteById(id);
    }

    public List<Trope> findTropeByGenre(Genre genre) {
        return tropeRepository.findTropeByGenre(genre);
    }




}