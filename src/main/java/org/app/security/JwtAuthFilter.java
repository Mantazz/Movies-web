package org.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app.service.UserDetailsServiceImpl;
import org.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Intercepts every HTTP request to handle JWT-based authentication
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Public (skip auth) – adjust as you need
        boolean isPublic =
                path.equals("/login.html") ||
                        path.equals("/register.html") ||
                        path.startsWith("/css/") ||
                        path.startsWith("/js/") ||
                        path.startsWith("/images/") ||
                        path.startsWith("/api/auth/") ||   // login/register JSON
                        path.equals("/login") || path.equals("/register") ||
                        path.equals("/signin") || path.equals("/signup");

        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        // -------- Get token (header first, then cookie) ----------
        String token = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("token".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        // -------- Validate token and set SecurityContext ----------
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Extract username from JWT (adjust method name to your JwtUtil)
            String username = jwtUtil.extractUsername(token); // aka getUsernameFromToken(token)

            if (username != null) {
                var userDetails = userDetailsService.loadUserByUsername(username);

                // validateToken signature differs across projects; use the one you have:
                boolean valid = jwtUtil.validateToken(token, userDetails.getUsername()); // or validateToken(token, userDetails)

                if (valid) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        // Always continue the chain
        filterChain.doFilter(request, response);
    }
}
