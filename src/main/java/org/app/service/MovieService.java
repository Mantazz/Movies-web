package org.app.service;

import org.app.DTO.MovieDTO;
import org.app.model.Movie;

import java.util.List;

public interface MovieService {
    Movie addMovie(MovieDTO movieDTO, Long userID);
    boolean deleteMovie(Long id, Long userId);
    boolean updateMovie(Long id, MovieDTO movieDTO, Long userId);
    List<Movie> getAllMovies(Long userId);

}
