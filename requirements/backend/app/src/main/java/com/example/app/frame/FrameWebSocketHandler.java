package com.example.app.frame;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

@Component
public class FrameWebSocketHandler extends BinaryWebSocketHandler {

    private static final String FRAME_SAVE_DIR = "/frames";

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        byte[] imageBytes = message.getPayload().array();

        System.out.println("WebSocket에서 바이트 수신! size = " + imageBytes.length + " bytes");

        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage img = ImageIO.read(bis);

        if (img != null) {
            System.out.println("Received image: width=" + img.getWidth() + ", height=" + img.getHeight());
            saveImage(img);
        } else {
            System.out.println("Failed to decode image.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket 연결 종료: " + session.getId() + ", 이유: " + status);
    }

    private void saveImage(BufferedImage img) {
        try {
            File directory = new File(FRAME_SAVE_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // 폴더가 없으면 생성
            }

            // 파일명에 UUID 추가
            String filename = FRAME_SAVE_DIR + "/frame_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ".jpg";
            File outputfile = new File(filename);

            ImageIO.write(img, "jpg", outputfile);
            System.out.println("Saved image: " + outputfile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to save image: " + e.getMessage());
        }
    }
}
