package com.example.app.frame;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/frames")
@RequiredArgsConstructor
public class FrameController {

    private final String saveDir = "/home/ubuntu/frames";  // 저장 경로

    @PostMapping
    public ResponseEntity<String> uploadFrame(@RequestBody FrameRequestDto requestDto) throws IOException {
        List<List<List<Integer>>> frame = requestDto.getFrame();  // RGB 배열 받기

        int height = frame.size();
        int width = frame.get(0).size();

        // BufferedImage 생성
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // RGB 값으로 픽셀 설정
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                List<Integer> rgb = frame.get(y).get(x);
                int r = rgb.get(0);
                int g = rgb.get(1);
                int b = rgb.get(2);
                int pixel = (r << 16) | (g << 8) | b;
                bufferedImage.setRGB(x, y, pixel);
            }
        }

        // 파일 저장
        String filename = "frame_" + System.currentTimeMillis() + ".jpg";
        Path savePath = Paths.get(saveDir, filename);
        Files.createDirectories(savePath.getParent());

        File outputFile = savePath.toFile();
        ImageIO.write(bufferedImage, "jpg", outputFile);

        // 저장된 파일 URL 반환
        String imageUrl = "/images/" + filename;
        return ResponseEntity.ok(imageUrl);
    }
}
