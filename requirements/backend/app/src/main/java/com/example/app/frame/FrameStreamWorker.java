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

    public void registerSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    @Override
    public void run() {
        System.out.println("▶ StreamWorker 시작: CCTV " + cctvId);
        while (true) {
            try {
                FrameData frame = queueManager.take(cctvId);
                BinaryMessage message = new BinaryMessage(frame.getImageBytes());

                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(message);
                        } catch (IOException e) {
                            System.err.println("[StreamWorker] 전송 실패: " + e.getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
