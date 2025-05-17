package com.example.app.frame;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Component
@RequiredArgsConstructor
public class FrameWebSocketHandler extends BinaryWebSocketHandler {

    private final FrameQueueManager queueManager;
    private final FrameStreamWorker streamWorker;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String path = session.getUri().getPath();

        if (path.startsWith("/ws/stream-view")) {
            streamWorker.registerSession(session);
            System.out.println("📺 프론트엔드 연결됨: " + session.getId());
        } else {
            System.out.println("📡 YOLO 연결됨: " + session.getId() + ", 경로: " + path);
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String path = session.getUri().getPath(); // 예: /ws/frame/101
        String[] parts = path.split("/");

        if (parts.length < 4) {
            System.err.println("[WebSocket] 잘못된 경로 형식: " + path);
            return;
        }

        String cctvId = parts[3]; // {cctvId}
        byte[] imageBytes = message.getPayload().array();
        long timestamp = System.currentTimeMillis();

        // ✅ 디버깅 로그
        System.out.println("[WebSocket] 프레임 수신: cctvId=" + cctvId + ", 크기=" + imageBytes.length);

        FrameData frame = new FrameData(cctvId, imageBytes, timestamp);
        queueManager.enqueue(frame);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        streamWorker.removeSession(session);
        System.out.println("❌ 연결 종료됨: " + session.getId() + " (" + status + ")");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("[WebSocket] 전송 오류 발생 - 세션: " + session.getId() + ", 오류: " + exception.getMessage());
    }
}
