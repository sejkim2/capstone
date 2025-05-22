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

    // 중복 세션 제거 + 새 세션 등록
    public void registerSession(WebSocketSession newSession) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.close();
                    System.out.println("기존 세션 종료됨: " + session.getId());
                } catch (IOException e) {
                    System.err.println("[StreamWorker] 세션 닫기 실패: " + e.getMessage());
                }
            }
        }

        sessions.clear();
        sessions.add(newSession);
        System.out.println("새 세션 등록됨: " + newSession.getId() + " (CCTV " + cctvId + ")");
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
        System.out.println("세션 제거됨: " + session.getId() + " (CCTV " + cctvId + ")");
    }

    @Override
    public void run() {
        System.out.println("StreamWorker 시작: CCTV " + cctvId);
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
