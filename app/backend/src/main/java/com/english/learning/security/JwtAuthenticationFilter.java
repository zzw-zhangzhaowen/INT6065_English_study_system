package com.english.learning.security;

import com.english.learning.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter
extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = this.extractToken(request);
            if (StringUtils.hasText((String)token) && this.jwtUtil.validateToken(token)) {
                String username = this.jwtUtil.getUsernameFromToken(token);
                Long userId = this.jwtUtil.getUserIdFromToken(token);
                String role = this.jwtUtil.getRoleFromToken(token);
                String authorityRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityRole);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
                authentication.setDetails(userId);
                SecurityContextHolder.getContext().setAuthentication((Authentication)authentication);
            }
        }
        catch (Exception e) {
            log.error("JWT\u8ba4\u8bc1\u5931\u8d25: {}",e.getMessage());
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText((String)bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
}

