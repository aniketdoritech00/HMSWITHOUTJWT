//package com.doritech.api.Config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//import com.doritech.api.Entity.BroadcastWebSocketHandler;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    private final BroadcastWebSocketHandler broadcastWebSocketHandler;
//
//    public WebSocketConfig(BroadcastWebSocketHandler broadcastWebSocketHandler) {
//        this.broadcastWebSocketHandler = broadcastWebSocketHandler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(broadcastWebSocketHandler, "/ws/broadcast")
//                .setAllowedOrigins("*"); // Configure CORS as needed
//    }
//}