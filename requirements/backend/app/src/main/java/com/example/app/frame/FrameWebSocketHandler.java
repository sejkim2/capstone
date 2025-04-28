package com.example.app.frame;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Component
public class FrameWebSocketHandler extends BinaryWebSocketHandler {

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        byte[] imageBytes = message.getPayload().array();

        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage img = ImageIO.read(bis);

        if (img != null) {
            System.out.println("Received image: width=" + img.getWidth() + ", height=" + img.getHeight());

            // /frames에 저장
            saveImage(img);
        } else {
            System.out.println("Failed to decode image.");
        }
    }

    private void saveImage(BufferedImage img) {
        try {
            // 경로를 /frames로 고정
            File directory = new File("/frames");
            if (!directory.exists()) {
                directory.mkdirs(); // 혹시 없으면 생성
            }

            // 파일명 규칙 설정: 현재 시간 기반
            String filename = "/frames/frame_" + System.currentTimeMillis() + ".jpg";
            File outputfile = new File(filename);

            ImageIO.write(img, "jpg", outputfile);
            System.out.println("Saved image: " + outputfile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to save image: " + e.getMessage());
        }
    }
}
