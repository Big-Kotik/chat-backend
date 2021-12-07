package com.kotik.big.chatbackend.security;

import com.kotik.big.chatbackend.security.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expirationTimeInMinutes;

    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date expiration = DateUtils.addMinutes(now, expirationTimeInMinutes);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getToken(HttpServletRequest request) throws JwtAuthenticationException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.matches("Bearer \\S+$")) {
            throw new JwtAuthenticationException(String.format("%s header is missing or invalid", HttpHeaders.AUTHORIZATION));
        }
        return header.split(" ")[1];
    }

    public String getUsername(String token) throws JwtAuthenticationException {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid", e);
        }
    }

}