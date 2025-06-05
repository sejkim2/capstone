package com.example.app.video;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public VideoPageResponseDto getFilteredVideos(Long cctvId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<Video> videoPage = videoRepository.findByCctvCctvIdAndStartTimeBetween(cctvId, start, end, pageable);

        List<VideoPreviewDto> content = videoPage.getContent().stream()
                .map(video -> new VideoPreviewDto(
                        generatePresignedUrl(video.getS3Path()),
                        generatePresignedUrl(video.getThumbnailPath()),
                        video.getStartTime()
                ))
                .collect(Collectors.toList());

        return new VideoPageResponseDto(
                content,
                videoPage.getNumber(),
                videoPage.getSize(),
                videoPage.getTotalPages(),
                videoPage.getTotalElements()
        );
    }

    private String generatePresignedUrl(String s3Path) {
    if (s3Path == null) return null;

    try {
        String key = s3Path.replace("s3://" + BUCKET_NAME + "/", "");

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10); // 10분

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(request);
        return url.toString();

    } catch (IllegalArgumentException e) {
        // 잘못된 인자 (key 또는 expiration)
        System.err.println("[ERROR] Invalid argument for presigned URL: " + e.getMessage());
        return null;

    } catch (com.amazonaws.services.s3.model.AmazonS3Exception s3e) {
        // S3 오류 (예: NoSuchBucket, AccessDenied)
        System.err.println("[S3 ERROR] " + s3e.getErrorCode() + ": " + s3e.getErrorMessage());
        return null;

    } catch (Exception e) {
        // 모든 기타 예외
        System.err.println("[ERROR] Failed to generate presigned URL: " + e.getMessage());
        return null;
    }
}

}
