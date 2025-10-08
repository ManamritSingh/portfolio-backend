package com.portfolio.backend.security;

import com.portfolio.backend.model.User;
import com.portfolio.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🔥 JwtAuthenticationFilter running: " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            System.out.println("⚠️ No Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ Authorization header does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7).trim(); // remove extra spaces/newlines
        System.out.println("🔑 Extracted JWT: " + jwt);

        String userEmail = jwtService.extractUsername(jwt);
        System.out.println("📧 Extracted username: " + userEmail);

        if (userEmail == null) {
            System.out.println("❌ Username could not be extracted from JWT");
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("⚠️ Security context already has authentication");
            filterChain.doFilter(request, response);
            return;
        }

        var user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            System.out.println("❌ User not found in database: " + userEmail);
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.isTokenValid(jwt, user)) {
            System.out.println("❌ JWT is invalid for user: " + userEmail);
            filterChain.doFilter(request, response);
            return;
        }

        // If we reach here, authentication is successful
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("✅ Authenticated user: " + userEmail);

        filterChain.doFilter(request, response);
        System.out.println("➡️ Finished filter chain for request: " + request.getRequestURI());
    }
}
