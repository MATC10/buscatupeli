package com.matc.buscatupeli.models;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_trope_id")
    private MovieTrope movieTrope;

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMovieTrope(MovieTrope movieTrope) {
        this.movieTrope = movieTrope;
    }
}