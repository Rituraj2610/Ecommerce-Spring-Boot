package com.rituraj.ecommerce.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(@Lazy JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);
        System.out.println("Token resolved in JwtAF: " + token);

        if (token != null && jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.extractClaims(token);
            String email = claims.getSubject();
            System.out.println("Entered 1st if statement of dofilter");
            System.out.println(email);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (userDetails != null) {
                System.out.println("Entered if statement of dofilter");
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                );

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("Authentication: " + auth);
                System.out.println("Authenticated User: " + auth.getPrincipal());
                System.out.println("User Roles: " + auth.getAuthorities());
            }else{
                System.out.println("USer not found");
            }
        }else{
            System.out.println("invalid token");
        }
        filterChain.doFilter(request, response);
    }

}