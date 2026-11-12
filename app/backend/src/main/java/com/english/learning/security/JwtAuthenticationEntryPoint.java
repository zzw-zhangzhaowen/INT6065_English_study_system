package com.english.learning.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint
implements AuthenticationEntryPoint {
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(401);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("code", 401);
        body.put("message", "\u672a\u6388\u6743\u8bbf\u95ee\uff0c\u8bf7\u5148\u767b\u5f55");
        body.put("data", null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue((OutputStream)response.getOutputStream(), body);
    }
}

