package org.app.service;

import org.app.DTO.MovieDTO;
import org.app.model.AppUser;
import org.app.model.Movie;
import org.app.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private final UserService userService;
    private final MovieRepository movieRepository;

    public MovieServiceImpl(UserService userService, MovieRepository movieRepository) {
        this.userService = userService;
        this.movieRepository = movieRepository;
    }

    private Movie mapToEntity(MovieDTO movieDTO) {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.title());
        movie.setLength(movieDTO.length().trim());
        movie.setNotes(movieDTO.notes());
        movie.setRating(movieDTO.rating());
        return movie;
    }


    @Override
    public Movie addMovie(MovieDTO movieDTO, Long userID) {
        Optional<AppUser> userOptional = userService.findUserById(userID);
        if(userOptional.isPresent()) {
            AppUser user = userOptional.get(); // getting not option but real user
            Movie movie = mapToEntity(movieDTO);
            movie.setUser(user);
            return movieRepository.save(movie);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public boolean deleteMovie(Long id, Long userId) {
        Optional<Movie> existingMovie = movieRepository.findByIdAndUserId(id, userId);
        if(existingMovie.isPresent()) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateMovie(Long id, MovieDTO updatedMovieDTO, Long userId) {
        Movie updatedMovie = mapToEntity(updatedMovieDTO);
        updatedMovie.setId(id);
        Optional<Movie> existingMovie = movieRepository.findByIdAndUserId(updatedMovie.getId(), userId);
        if(existingMovie.isPresent()) {
            updatedMovie.setUser(existingMovie.get().getUser());
            movieRepository.save(updatedMovie);
            return true;
        }
        return false;
    }

    @Override
    public List<Movie> getAllMovies(Long userId) {
        return movieRepository.findByUserId(userId);
    }
}
