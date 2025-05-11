package com.example.app.frame;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FrameWebSocketHandler extends BinaryWebSocketHandler {

    private static final String FRAME_SAVE_DIR = "/frames";  // base 디렉토리
    private final ExecutorService executor = Executors.newFixedThreadPool(2); // 비동기 저장용

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // URI에서 videoId 추출
        String path = session.getUri().getPath(); // 예: /ws/frame/cctv101
        String[] parts = path.split("/");
        String videoId = parts[parts.length - 1];

        byte[] imageBytes = message.getPayload().array();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage img = ImageIO.read(bis);

        if (img != null) {
            System.out.println("Received image from [" + videoId + "]: " +
                "width=" + img.getWidth() + ", height=" + img.getHeight());
            saveImageAsync(img, videoId);
        } else {
            System.out.println("Failed to decode image from [" + videoId + "].");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket 연결 종료: " + session.getId() + ", 이유: " + status);
    }

    private void saveImageAsync(BufferedImage img, String videoId) {
        executor.submit(() -> {
            try {
                File directory = new File(FRAME_SAVE_DIR + "/" + videoId);
                if (!directory.exists()) {
                    directory.mkdirs(); // 디렉토리 생성
                }

                String filename = String.format("%s/%d_%s.jpg",
                        directory.getAbsolutePath(),
                        System.currentTimeMillis(),
                        UUID.randomUUID());

                ImageIO.write(img, "jpg", new File(filename));
                System.out.println("Saved image: " + filename);
            } catch (IOException e) {
                System.out.println("Failed to save image for [" + videoId + "]: " + e.getMessage());
            }
        });
    }
}
