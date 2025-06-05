package com.example.app.video;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VideoPreviewDto {
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDateTime timestamp;
}