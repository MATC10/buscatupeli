package com.matc.buscatupeli.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class MovieTrope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "trope_id")
    private Trope trope;

    private int voteCount;

    @OneToMany(mappedBy = "movieTrope")
    private List<Vote> votes;

    public Long getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Trope getTrope() {
        return trope;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public List<Vote> getVotes() {
        return votes;
    }
}