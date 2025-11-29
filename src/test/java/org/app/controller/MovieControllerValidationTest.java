package org.app.controller;

import org.app.model.AppUser;
import org.app.service.MovieService;
import org.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MovieController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.app.config.SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.app.security.JwtAuthFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MovieService movieService;
    @MockitoBean
    private Authentication authentication;

    private AppUser mockUser;

    @BeforeEach
    void setup() {
        when(authentication.getName()).thenReturn("john");
        mockUser = new AppUser();
        mockUser.setId(1L);
        mockUser.setUsername("john");
        when(userService.findUserByUsername("john")).thenReturn(mockUser);
    }

    // Valid input test
    @Test
    @DisplayName("POST /api/movie/add -> 201 Created when valid input is provided")
    void addMovie_ValidInput_ReturnsCreated() throws Exception {
        String validJson = """
            {
              "title": "Matrix",
              "length": "2:16",
              "notes": "Neo forever",
              "rating": 5
            }
            """;

        mockMvc.perform(post("/api/movie/add")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated());
    }

    // Invalid input test
    @Nested
    @DisplayName("POST /api/movie/add -> 400 Bad Request when validation fails")
    class InvalidInputs {

        @Test
        @DisplayName("Blank title should fail (@NotBlank)")
        void blankTitle_ShouldFail() throws Exception {
            String invalidJson = """
                {
                  "title": "",
                  "length": "2:30",
                  "notes": "good",
                  "rating": 4
                }
                """;
            mockMvc.perform(post("/api/movie/add")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Title too long should fail (@Size max=50)")
        void longTitle_ShouldFail() throws Exception {
            String invalidJson = """
                {
                  "title": "%s",
                  "length": "1:45",
                  "notes": "ok",
                  "rating": 3
                }
                """.formatted("A".repeat(60));

            mockMvc.perform(post("/api/movie/add")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Invalid length format should fail (@Pattern h:mm)")
        void invalidLength_ShouldFail() throws Exception {
            String invalidJson = """
                {
                  "title": "Titanic",
                  "length": "abc",
                  "notes": "ok",
                  "rating": 5
                }
                """;

            mockMvc.perform(post("/api/movie/add")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Notes too long should fail (@Size max=500)")
        void longNotes_ShouldFail() throws Exception {
            String invalidJson = """
                {
                  "title": "Avatar",
                  "length": "3:00",
                  "notes": "%s",
                  "rating": 5
                }
                """.formatted("A".repeat(600));

            mockMvc.perform(post("/api/movie/add")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Rating < 1 should fail (@Min 1)")
        void ratingTooLow_ShouldFail() throws Exception {
            String invalidJson = """
                {
                  "title": "Inception",
                  "length": "2:30",
                  "notes": "dream",
                  "rating": 0
                }
                """;

            mockMvc.perform(post("/api/movie/add")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Rating > 5 should fail (@Max 5)")
        void ratingTooHigh_ShouldFail() throws Exception {
            String invalidJson = """
                {
                  "title": "Interstellar",
                  "length": "2:30",
                  "notes": "epic",
                  "rating": 10
                }
                """;

            mockMvc.perform(post("/api/movie/add")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }
}
