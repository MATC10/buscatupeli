package com.matc.buscatupeli.services;

import com.matc.buscatupeli.models.MovieTrope;
import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.models.Vote;
import com.matc.buscatupeli.repositories.MovieTropeRepository;
import com.matc.buscatupeli.repositories.VoteRepository;
import com.matc.buscatupeli.repositories.impl.VoteRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class VoteService {

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    private VoteRepositoryImpl voteRepositoryImpl;


    public ArrayList<Vote> findAll() {
        return (ArrayList<Vote>) voteRepository.findAll();
    }

    public Vote findById(long id) {
        return voteRepository.findById(id).orElse(null);
    }

    public Vote save(Vote vote) {
        return voteRepository.save(vote);
    }

    public void deleteById(long id) {
        voteRepository.deleteById(id);
    }

    public void voteMovieTrope(int tropeId, int movieId, int voteValue) {
        voteRepositoryImpl.voteMovieTrope(tropeId, movieId, voteValue);
    }

    public void proposeTrope(int tropeId, int movieId){
        voteRepositoryImpl.proposeTrope(tropeId, movieId);
    }
}