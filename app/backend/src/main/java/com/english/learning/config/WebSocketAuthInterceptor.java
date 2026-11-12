package com.english.learning.config;

import com.english.learning.util.JwtUtil;
import java.security.Principal;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WebSocketAuthInterceptor
implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    private final JwtUtil jwtUtil;

    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (!StringUtils.hasText((String)token)) {
                log.warn("WebSocket\u8fde\u63a5\u7f3a\u5c11Authorization\u5934\uff0c\u62d2\u7edd\u8fde\u63a5");
                throw new MessageDeliveryException("\u672a\u63d0\u4f9b\u8ba4\u8bc1\u4ee4\u724c");
            }
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            try {
                if (!this.jwtUtil.validateToken(token)) {
                    log.warn("WebSocket\u4ee4\u724c\u65e0\u6548\uff0c\u62d2\u7edd\u8fde\u63a5");
                    throw new MessageDeliveryException("\u8ba4\u8bc1\u4ee4\u724c\u65e0\u6548\u6216\u5df2\u8fc7\u671f");
                }
                String username = this.jwtUtil.getUsernameFromToken(token);
                Long userId = this.jwtUtil.getUserIdFromToken(token);
                String role = this.jwtUtil.getRoleFromToken(token);
                String authorityRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority(authorityRole)));
                auth.setDetails(userId);
                accessor.setUser((Principal)auth);
                log.debug("WebSocket\u8ba4\u8bc1\u6210\u529f: userId={}, username={}",userId, username);
            }
            catch (MessageDeliveryException e) {
                throw e;
            }
            catch (Exception e) {
                log.warn("WebSocket\u8ba4\u8bc1\u5931\u8d25: {}",e.getMessage());
                throw new MessageDeliveryException("\u8ba4\u8bc1\u5931\u8d25: " + e.getMessage());
            }
        }
        if (accessor != null && (StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) && accessor.getUser() == null) {
            log.warn("\u672a\u8ba4\u8bc1\u7684WebSocket\u7528\u6237\u5c1d\u8bd5\u53d1\u9001/\u8ba2\u9605\u6d88\u606f\uff0c\u5df2\u62d2\u7edd");
            throw new MessageDeliveryException("\u672a\u8ba4\u8bc1\uff0c\u8bf7\u5148\u8fde\u63a5");
        }
        return message;
    }

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
}

