package com.example.app.video;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class VideoPageResponseDto {
    private List<VideoPreviewDto> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
}