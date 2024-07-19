package com.matc.buscatupeli.repositories.impl;

import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public class VoteRepositoryImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void voteMovieTrope(int tropeId, int movieId,  int voteValue) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserName);
        Long userId = currentUser.getId();

        // Obtén los roles del usuario actual
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Primero, actualizamos el vote_count en la tabla movie_trope
        entityManager.createNativeQuery("UPDATE movie_trope SET vote_count = vote_count + ? WHERE trope_id = ? AND movie_id = ?")
                .setParameter(1, voteValue)
                .setParameter(2, tropeId)
                .setParameter(3, movieId)
                .executeUpdate();

        // Obtenemos el movie_trope_id correcto
        Object movieTropeId = entityManager.createNativeQuery("SELECT id FROM movie_trope WHERE trope_id = ? AND movie_id = ?")
                .setParameter(1, tropeId)
                .setParameter(2, movieId)
                .getSingleResult();

        // Luego, comprobamos si el registro ya existe en la tabla vote
        List<Object> voteExists = entityManager.createNativeQuery("SELECT 1 FROM vote WHERE movie_trope_id = ? AND user_id = ?")
                .setParameter(1, movieTropeId)
                .setParameter(2, userId)
                .getResultList();

        // Verifica si el usuario es un administrador
        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!voteExists.isEmpty() && !isAdmin) {
            // Si existe y el usuario no es un administrador, restablecemos el vote_count en la tabla movie_trope
            entityManager.createNativeQuery("UPDATE movie_trope SET vote_count = vote_count - ? WHERE trope_id = ? AND movie_id = ?")
                    .setParameter(1, voteValue)
                    .setParameter(2, tropeId)
                    .setParameter(3, movieId)
                    .executeUpdate();
        } else {
            // Si no existe o el usuario es un administrador, añadimos el registro a la tabla vote
            entityManager.createNativeQuery("INSERT INTO vote (movie_trope_id, user_id) VALUES (?, ?)")
                    .setParameter(1, movieTropeId)
                    .setParameter(2, userId)
                    .executeUpdate();
        }
    }

    @Transactional
    public void proposeTrope(int tropeId, int movieId) {
        // Comprueba si existe un registro en la tabla movie_trope con el movie_id y trope_id proporcionados
        List<Object> movieTropeExists = entityManager.createNativeQuery("SELECT 1 FROM movie_trope WHERE trope_id = ? AND movie_id = ?")
                .setParameter(1, tropeId)
                .setParameter(2, movieId)
                .getResultList();

        // Si no existe tal registro, crea uno nuevo
        if (movieTropeExists.isEmpty()) {
            entityManager.createNativeQuery("INSERT INTO movie_trope (vote_count, trope_id, movie_id) VALUES (?, ?, ?)")
                    .setParameter(1, 1)
                    .setParameter(2, tropeId)
                    .setParameter(3, movieId)
                    .executeUpdate();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserName);
        Long userId = currentUser.getId();

        // Obtenemos el movie_trope_id correcto
        Object movieTropeId = entityManager.createNativeQuery("SELECT id FROM movie_trope WHERE trope_id = ? AND movie_id = ?")
                .setParameter(1, tropeId)
                .setParameter(2, movieId)
                .getSingleResult();

        // Luego, comprobamos si el registro ya existe en la tabla vote (es decir, si tu usuario ya ha votado por ese trope)
        List<Object> voteExists = entityManager.createNativeQuery("SELECT 1 FROM vote WHERE movie_trope_id = ? AND user_id = ?")
                .setParameter(1, movieTropeId)
                .setParameter(2, userId)
                .getResultList();

        // Obtenemos el vote_count del trope en la tabla movie_trope
        Object voteCount = entityManager.createNativeQuery("SELECT vote_count FROM movie_trope WHERE trope_id = ? AND movie_id = ?")
                .setParameter(1, tropeId)
                .setParameter(2, movieId)
                .getSingleResult();

        if (!voteExists.isEmpty() && ((Number) voteCount).intValue() < 1) {
            // Si existe y el vote_count es menor que 1, permitimos que el usuario vote con un voto positivo
            entityManager.createNativeQuery("UPDATE movie_trope SET vote_count = vote_count + 1 WHERE trope_id = ? AND movie_id = ?")
                    .setParameter(1, tropeId)
                    .setParameter(2, movieId)
                    .executeUpdate();
        } else if (voteExists.isEmpty()) {
            // Si no existe, añadimos el registro a la tabla vote
            entityManager.createNativeQuery("INSERT INTO vote (movie_trope_id, user_id) VALUES (?, ?)")
                    .setParameter(1, movieTropeId)
                    .setParameter(2, userId)
                    .executeUpdate();
        }
    }
}