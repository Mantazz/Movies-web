package org.app.config;

import org.app.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/login.html", "/register.html",
//                                "/css/**", "/js/**", "/images/**").permitAll()
////                        .requestMatchers(HttpMethod.GET, "/login", "/register", "/signin", "/signup").permitAll()
////                        .requestMatchers(HttpMethod.POST, "/login", "/register", "/api/auth/**", "/api/movie/**").permitAll()
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api").authenticated() // REST API
//                        .requestMatchers("/movies/**").authenticated() // Thymeleaf pages
//                        .anyRequest().hasRole("USER")
//                )
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Makes our custom filter to be executed before the username and password authentication filter

                // Keep CSRF for MVC, ignore it for REST (POST/PUT/DELETE to /api/**)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // public static pages + assets
                        .requestMatchers("/login.html", "/register.html",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        // public auth endpoints (JSON login/register)
                        .requestMatchers("/api/auth/**").permitAll()

                        // MVC pages (Thymeleaf)
                        .requestMatchers("/movies/**").authenticated()

                        // REST API (protect all, except /api/auth/** above)
                        .requestMatchers("/api/**").authenticated()

                        // everything else: allow for now (helps isolate)
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
