package com.example.app.video;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping
    public List<VideoResponseDto> getVideosByCctvId(@RequestParam Long cctvId) {
        return videoService.getVideosByCctvId(cctvId);
    }
}
