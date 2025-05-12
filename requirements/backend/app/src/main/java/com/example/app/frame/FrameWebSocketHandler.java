package com.example.app.frame;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FrameWebSocketHandler extends BinaryWebSocketHandler {

    private static final String FRAME_SAVE_DIR = "/frames";  // base 디렉토리
    private final ExecutorService executor = Executors.newFixedThreadPool(4); // 비동기 저장용

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // URI에서 cctvId 추출
        String path = session.getUri().getPath(); // 예: /ws/frame/cctv101
        String[] parts = path.split("/");
        String cctvId = parts[parts.length - 1];

        byte[] imageBytes = message.getPayload().array();

        // 디코딩 없이 바로 저장
        saveImageAsync(imageBytes, cctvId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket 연결 종료: " + session.getId() + ", 이유: " + status);
    }

    private void saveImageAsync(byte[] imageBytes, String cctvId) {
        executor.submit(() -> {
            try {
                File directory = new File(FRAME_SAVE_DIR + "/" + cctvId);
                if (!directory.exists()) {
                    directory.mkdirs(); // 디렉토리 생성
                }

                String filename = String.format("%s/%d_%s.jpg",
                        directory.getAbsolutePath(),
                        System.currentTimeMillis(),
                        UUID.randomUUID());

                Files.write(Paths.get(filename), imageBytes);
                // System.out.println("Saved image: " + filename);
            } catch (IOException e) {
                System.out.println("Failed to save image for [" + cctvId + "]: " + e.getMessage());
            }
        });
    }
}
