package com.example.bankcards.util;

import com.example.bankcards.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Setter
@Getter
public class JwtAccessTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    private static final String ROLES = "roles";

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()).getAuthority())
                .toList();

        claims.put(ROLES, rolesList);

        Date issuedDate = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(new Date(issuedDate.getTime() + jwtLifetime.toMillis()))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get(ROLES, List.class);
    }
}
