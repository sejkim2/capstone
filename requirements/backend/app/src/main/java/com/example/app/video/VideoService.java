package com.example.app.video;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final AmazonS3 amazonS3;

    private static final String BUCKET_NAME = "capstone-cctv-bucket";

    @Transactional
    public List<VideoResponseDto> getVideosByCctvId(Long cctvId) {
        List<Video> videos = videoRepository.findByCctvCctvId(cctvId);

        return videos.stream()
                .map(video -> new VideoResponseDto(
                        video.getVideoId(),
                        generatePresignedUrl(video.getS3Path()),
                        video.getStartTime(),
                        video.getEndTime()
                ))
                .collect(Collectors.toList());
    }

    private String generatePresignedUrl(String s3Path) {
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10); // 10분 유효

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, s3Path)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(request);
        return url.toString();
    }
}
