package com.example.app.frame;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Component
@RequiredArgsConstructor
public class FrameWebSocketHandler extends BinaryWebSocketHandler {

    private final FrameQueueManager queueManager;
    private final FrameStreamWorkerRegistry streamRegistry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String path = session.getUri().getPath();

        if (path.startsWith("/ws/stream-view")) {
            String query = session.getUri().getQuery(); // 예: cctvId=101
            if (query != null && query.startsWith("cctvId=")) {
                String cctvId = query.split("=")[1];
                streamRegistry.registerSession(cctvId, session);
                System.out.println("프론트 연결됨: " + session.getId() + ", CCTV: " + cctvId);
            }
        } else {
            System.out.println("YOLO 연결됨: " + session.getId() + ", 경로: " + path);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String path = session.getUri().getPath(); // /ws/frame/101
        String[] parts = path.split("/");
        if (parts.length < 4) return;

        String cctvId = parts[3];
        byte[] imageBytes = message.getPayload().array();
        long timestamp = System.currentTimeMillis();

        queueManager.enqueue(new FrameData(cctvId, imageBytes, timestamp));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        streamRegistry.removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("[WebSocket] 오류 - " + exception.getMessage());
    }
}

