package com.english.learning.config;

import com.english.learning.config.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
implements WebSocketMessageBrokerConfigurer {
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(new String[]{"/topic", "/queue"});
        config.setApplicationDestinationPrefixes(new String[]{"/app"});
        config.setUserDestinationPrefix("/user");
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(new String[]{"/ws"}).setAllowedOriginPatterns(new String[]{"*"}).withSockJS();
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor[]{this.webSocketAuthInterceptor});
    }

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }
}

