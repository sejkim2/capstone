package com.example.app.frame;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FrameWebSocketHandler extends BinaryWebSocketHandler {

    private static final String FRAME_SAVE_DIR = "/frames";

    // 저장을 비동기로 처리할 스레드 풀 (2개 쓰레드 사용)
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        byte[] imageBytes = message.getPayload().array();

        System.out.println("WebSocket에서 바이트 수신! size = " + imageBytes.length + " bytes");

        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage img = ImageIO.read(bis);

        if (img != null) {
            System.out.println("Received image: width=" + img.getWidth() + ", height=" + img.getHeight());
            saveImageAsync(img);
        } else {
            System.out.println("Failed to decode image.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket 연결 종료: " + session.getId() + ", 이유: " + status);
    }

    private void saveImageAsync(BufferedImage img) {
        executor.submit(() -> {
            try {
                File directory = new File(FRAME_SAVE_DIR);
                if (!directory.exists()) {
                    directory.mkdirs(); // 디렉토리가 없으면 생성
                }

                String filename = FRAME_SAVE_DIR + "/frame_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ".jpg";
                File outputfile = new File(filename);

                ImageIO.write(img, "jpg", outputfile);
                System.out.println("Saved image: " + outputfile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to save image: " + e.getMessage());
            }
        });
    }
}
