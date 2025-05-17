package com.example.app.frame;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
public class FrameStreamWorker implements Runnable {

    private final FrameQueueManager queueManager;
    private final Set<WebSocketSession> frontendSessions = new CopyOnWriteArraySet<>();

    public void registerSession(WebSocketSession session) {
        frontendSessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        frontendSessions.remove(session);
    }

    @Override
    public void run() {
        while (true) {
            try {
                FrameData frame = queueManager.take(); // 큐에서 하나 꺼냄
                BinaryMessage message = new BinaryMessage(frame.getImageBytes());

                for (WebSocketSession session : frontendSessions) {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(message);
                        } catch (IOException e) {
                            System.out.println("[StreamWorker] 전송 실패: " + e.getMessage());
                        }
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        frontendSessions.clear();
    }
}
