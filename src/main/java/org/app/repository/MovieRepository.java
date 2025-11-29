package org.app.repository;

import org.app.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByIdAndUserId(Long id, Long userId);
    List<Movie> findByUserId(Long userId);
}
