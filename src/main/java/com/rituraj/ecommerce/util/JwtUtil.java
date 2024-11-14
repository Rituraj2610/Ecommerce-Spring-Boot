package com.rituraj.ecommerce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey; // Ideally from application properties
    private final long expirationMs = 86400000; // 1 day

//    @PostConstruct
//    protected void init() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//    }


    public JwtUtil(SecretKey secretKey) {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(String email, String role, String userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", role)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            System.out.println("true validation in jwutil");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("true validation in jwutil");
            return false;
        }
    }

    public String extractUserId(String token) {
        return extractClaims(token).get("id", String.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("roles", String.class);
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        System.out.println(bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            // Extract from cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT_TOKEN".equals(cookie.getName())) {
                        System.out.println("Cookie value"+cookie.getValue());
                        return cookie.getValue();
                    }
                }
            }else{
                System.out.println("No cookie token found");
            }
        }
        return null;
    }
}

//@Component
//public class JwtUtil {
//    private final String secretKey = "yourSecretKey"; // Ideally from application properties
//    private final long expirationMs = 86400000; // 1 day
//
//    public String generateToken(String email, String role) {
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("roles", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }
//
//    public Claims extractClaims(String token) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
//}

