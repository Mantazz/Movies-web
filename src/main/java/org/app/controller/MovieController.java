package org.app.controller;

import jakarta.validation.Valid;
import org.app.DTO.MovieDTO;
import org.app.model.AppUser;
import org.app.model.Movie;
import org.app.service.MovieService;
import org.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final UserService userService;
    private final MovieService movieService;

    public MovieController(UserService userService, MovieService movieService) {
        this.userService = userService;
        this.movieService = movieService;
    }

    @PostMapping("/add")
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody MovieDTO movieDTO,
                                          Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findUserByUsername(username);
        Movie newMovie = movieService.addMovie(movieDTO, user.getId());
        return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Movie> deleteMovie(@PathVariable Long id,
                                             Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findUserByUsername(username);
        boolean isDeleted = movieService.deleteMovie(id, user.getId());
        if(isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id,
                                             @Valid @RequestBody MovieDTO movieDTO,
                                             Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findUserByUsername(username);
        boolean isUpdated = movieService.updateMovie(id, movieDTO, user.getId());
        if(isUpdated) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies(Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findUserByUsername(username);
        List<Movie> movies = movieService.getAllMovies(user.getId());
        if(movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(movies);
        }

    }
}