//package com.doritech.api.Entity;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.Set;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@Component
//public class BroadcastWebSocketHandler extends TextWebSocketHandler {
//
//    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.add(session);
//        System.out.println("WebSocket connection established: " + session.getId());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);
//        System.out.println("WebSocket connection closed: " + session.getId());
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        // Handle incoming messages from clients if needed
//        System.out.println("Received message from client: " + message.getPayload());
//    }
//
//    public void broadcast(String message) {
//        for (WebSocketSession session : sessions) {
//            if (session.isOpen()) {
//                try {
//                    session.sendMessage(new TextMessage(message));
//                } catch (IOException e) {
//                    System.err.println("Error broadcasting to session " + session.getId() + ": " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    public int getConnectedClientsCount() {
//        return sessions.size();
//    }
//}