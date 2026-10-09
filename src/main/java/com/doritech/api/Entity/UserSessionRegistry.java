package com.doritech.api.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class UserSessionRegistry {
    private final Map<String, List<HttpSession>> userSessions = new ConcurrentHashMap<>();

    public void registerSession(String email, HttpSession session) {
        session.setAttribute("email", email); // useful for cleanup
        userSessions.computeIfAbsent(email, k -> new CopyOnWriteArrayList<>()).add(session);
        System.out.println("✅ Registering session " + session.getId() + " for " + email);
    }

    public void removeSession(String email, HttpSession session) {
        List<HttpSession> sessions = userSessions.get(email);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(email);
            }
        }
    }

    public List<HttpSession> getSessions(String email) {
        return userSessions.getOrDefault(email, Collections.emptyList());
    }

    public void invalidateAllSessions(String email) {
        List<HttpSession> sessions = new ArrayList<>(getSessions(email)); // avoid concurrent modification
        for (HttpSession session : sessions) {
            try {
                System.out.println("❌ Invalidating session " + session.getId() + " for " + email);
                session.invalidate();
            } catch (Exception ignored) {}
        }
        userSessions.remove(email);
    }
}
