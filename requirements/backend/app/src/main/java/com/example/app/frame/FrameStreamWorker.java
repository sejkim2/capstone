package com.example.app.frame;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class FrameStreamWorker implements Runnable {

    private final String cctvId;
    private final FrameQueueManager queueManager;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    // 여러 사용자의 세션을 모두 유지
    public void registerSession(WebSocketSession newSession) {
        sessions.add(newSession);
        System.out.println("세션 추가됨: " + newSession.getId() + " (CCTV " + cctvId + ")");
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
        System.out.println("세션 제거됨: " + session.getId() + " (CCTV " + cctvId + ")");
    }

    @Override
public void run() {
    System.out.println("StreamWorker 시작: CCTV " + cctvId);
    while (!Thread.currentThread().isInterrupted()) {
        try {
            FrameData frame = queueManager.take(cctvId);
            BinaryMessage message = new BinaryMessage(frame.getImageBytes());

            for (WebSocketSession session : sessions) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(message);
                    } else {
                        removeSession(session);
                    }
                } catch (IOException | IllegalStateException e) {
                    System.err.println("[StreamWorker] 전송 실패, 세션 제거: " + session.getId() + " - " + e.getMessage());
                    removeSession(session);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}
}
