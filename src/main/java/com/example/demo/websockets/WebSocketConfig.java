package com.example.demo.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocketConfig class configures the WebSocket communication for the application.
 * This configuration enables WebSocket support and registers handlers for WebSocket
 * connections, defining endpoint mappings and connection policies.
 *
 * <p>Through the {@code @EnableWebSocket} annotation, this class activates WebSocket
 * handling, allowing real-time communication between the client and server. The main
 * purpose of this configuration is to define a WebSocket endpoint and set allowed origins
 * to facilitate cross-origin requests if needed.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * Registers WebSocket handlers by mapping the handler to a specified endpoint.
     * This method is part of the {@link WebSocketConfigurer} interface and is responsible
     * for associating the WebSocket handler with an endpoint URL, making it accessible to
     * clients.
     *
     * <p>The handler is registered at the "/ws" endpoint, and the allowed origins are set
     * to "*", allowing connections from any origin. This is useful for development and
     * testing but should be configured carefully for production environments to restrict
     * cross-origin access as needed.
     *
     * @param registry the WebSocketHandlerRegistry that handles WebSocket endpoint mappings
     */
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(), "/ws").setAllowedOrigins("*");
    }
}
