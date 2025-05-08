package com.example.app.frame;

import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/frames")
public class FrameViewController {

    private static final String FRAME_SAVE_DIR = "/frames";
    private static final int MAX_FILES = 40; // 최신 100개만 노출

    /**
     * 단일 이미지 반환
     */
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFrame(@PathVariable String filename) {
        try {
            File file = new File(FRAME_SAVE_DIR + "/" + filename);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 최신 이미지 목록을 HTML로 반환 (최대 100개)
     */
    @GetMapping("/html")
    public ResponseEntity<String> listFramesAsHtml() {
        File dir = new File(FRAME_SAVE_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg"));

        if (files == null || files.length == 0) {
            return ResponseEntity.ok("<html><body><h2>No images found</h2></body></html>");
        }

        // 최신순 정렬 후 제한
        List<File> latestFiles = Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .limit(MAX_FILES)
                .collect(Collectors.toList());

        // HTML 생성
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset=\"UTF-8\"></head><body>");
        html.append("<h2>Latest ").append(MAX_FILES).append(" Frames</h2><ul>");

        for (File file : latestFiles) {
            String name = file.getName();
            html.append("<li><a href=\"/frames/")
                .append(name)
                .append("\" target=\"_blank\">")
                .append(name)
                .append("</a></li>");
        }

        html.append("</ul></body></html>");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html.toString());
    }
}