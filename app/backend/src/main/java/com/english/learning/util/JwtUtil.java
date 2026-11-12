package com.english.learning.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    @Value(value="${jwt.secret}")
    private String secret;
    @Value(value="${jwt.expiration}")
    private Long expiration;

    public String generateToken(String username, Long userId, String role) {
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + this.expiration)).signWith((Key)this.getSignKey()).compact();
    }

    public String getUsernameFromToken(String token) {
        return this.getClaimsFromToken(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = this.getClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    public String getRoleFromToken(String token) {
        Claims claims = this.getClaimsFromToken(token);
        return claims.get("role").toString();
    }

    public boolean validateToken(String token) {
        try {
            this.getClaimsFromToken(token);
            return !this.isTokenExpired(token);
        }
        catch (JwtException | IllegalArgumentException e) {
            log.error("Token\u9a8c\u8bc1\u5931\u8d25: {}",e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = this.getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims getClaimsFromToken(String token) {
        return (Claims)Jwts.parserBuilder().setSigningKey((Key)this.getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor((byte[])keyBytes);
    }
}

