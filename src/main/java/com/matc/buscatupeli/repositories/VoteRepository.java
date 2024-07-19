package com.matc.buscatupeli.repositories;

import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.models.Vote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository <Vote, Long> {
}
