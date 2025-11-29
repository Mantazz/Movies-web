package org.app.controller;

import org.app.DTO.MovieDTO;
import org.app.model.AppUser;
import org.app.model.Movie;
import org.app.service.MovieService;
import org.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.app.config.SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.app.security.JwtAuthFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MovieService movieService;
    @MockitoBean
    private Authentication authentication;

    private AppUser mockUser;

    @Autowired
    MovieControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setupAuth() {
        authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("john");

        mockUser = new AppUser();
        mockUser.setId(1L);
        mockUser.setUsername("john");

        when(userService.findUserByUsername("john")).thenReturn(mockUser);
    }

    @Test
    @DisplayName("POST /api/movie/add -> 201 Created and returns saved movie")
    void addMovie_ReturnsMovie() throws Exception {
        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("Inception");
        savedMovie.setLength("2:30");
        savedMovie.setNotes("perfect");
        savedMovie.setRating(5);

        when(movieService.addMovie(any(MovieDTO.class), eq(1L))).thenReturn(savedMovie);

        mockMvc.perform(post("/api/movie/add")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Inception",
                                  "length": "2:30",
                                  "notes": "perfect",
                                  "rating": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.length").value("2:30"))
                .andExpect(jsonPath("$.notes").value("perfect"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("GET /api/movie/all -> 200 OK with list when user has movies")
    void getAllMovies_ReturnsList() throws Exception {
        Movie m1 = new Movie();
        m1.setId(10L);
        m1.setTitle("Interstellar");
        m1.setLength("2:49");
        m1.setNotes("wow");
        m1.setRating(5);

        Movie m2 = new Movie();
        m2.setId(11L);
        m2.setTitle("Dune");
        m2.setLength("2:35");
        m2.setNotes("spice");
        m2.setRating(4);

        when(movieService.getAllMovies(1L)).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/movie/all").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Interstellar"))
                .andExpect(jsonPath("$[1].title").value("Dune"));
    }

    @Test
    @DisplayName("GET /api/movie/all -> 204 No Content when user has no movies")
    void getAllMovies_NoContent() throws Exception {
        when(movieService.getAllMovies(1L)).thenReturn(List.of());
        mockMvc.perform(get("/api/movie/all").principal(authentication))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /api/movie/edit/{id} -> 200 OK on successful update")
    void updateMovie_Success() throws Exception {
        when(movieService.updateMovie(eq(10L), any(MovieDTO.class), eq(1L))).thenReturn(true);

        mockMvc.perform(put("/api/movie/edit/{id}", 10L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Interstellar (Extended)",
                                  "length": "2:55",
                                  "notes": "still wow",
                                  "rating": 5
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/movie/delete/{id} -> 204 No Content on successful delete")
    void deleteMovie_Success() throws Exception {
        when(movieService.deleteMovie(eq(10L), eq(1L))).thenReturn(true);

        mockMvc.perform(delete("/api/movie/delete/{id}", 10L)
                        .principal(authentication))
                .andExpect(status().isNoContent());
    }
}
