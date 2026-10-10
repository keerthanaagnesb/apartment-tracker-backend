package com.apartment.tracker.security;

import com.apartment.tracker.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs on every request: reads "Authorization: Bearer <token>", verifies it,
 * and tells Spring Security who the caller is. (Deliberately NOT a @Component —
 * it is created inside SecurityConfig so it only runs once, inside the security chain.)
 */
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            jwtService.validateAndGetEmail(header.substring(7)).ifPresent(email -> {
                try {
                    UserDetails details = userDetailsService.loadUserByUsername(email);
                    if (details.isEnabled() && SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
                    }
                } catch (UsernameNotFoundException ignored) {
                    // token for a user that no longer exists -> treated as not logged in
                }
            });
        }
        chain.doFilter(request, response);
    }
}
