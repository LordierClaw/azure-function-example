package me.lordierclaw.azfe.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.lordierclaw.azfe.model.User;

import java.util.Date;

public class JwtUtil {
    private static String SECRET_KEY = System.getenv("secret");

    public static String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 3000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    private static Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getBody();
    }

    public static String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public static Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
