package com.example.app.frame;

import com.amazonaws.services.s3.AmazonS3;
import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.video.Video;
import com.example.app.video.VideoRepository;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
public class FrameSaveWorker implements Runnable {

    private final String cctvId;
    private final FrameQueueManager queueManager;
    private final AmazonS3 amazonS3;
    private final CctvRepository cctvRepository;
    private final VideoRepository videoRepository;

    private static final int FRAME_THRESHOLD = 400;
    private static final int FRAME_RATE = 15;
    private static final String VIDEO_BASE_PATH = "/videos";
    private static final String THUMBNAIL_BASE_PATH = "/thumbnails";
    private static final String BUCKET_NAME = "capstone-cctv-bucket";

    private final List<FrameData> buffer = new ArrayList<>();

    @Override
    public void run() {
        System.out.println("▶ FrameSaveWorker 시작: CCTV " + cctvId);
        while (true) {
            try {
                FrameData frame = queueManager.take(cctvId); // CCTV별 큐 사용
                buffer.add(frame);

                // System.out.println("[FrameSaveWorker] cctvId=" + cctvId + ", 프레임 수=" + buffer.size());

                if (buffer.size() >= FRAME_THRESHOLD) {
                    List<FrameData> copy = new ArrayList<>(buffer);
                    buffer.clear();
                    processFrames(cctvId, copy);
                }

            } catch (Exception e) {
                System.err.println("[FrameSaveWorker] 오류: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processFrames(String cctvIdStr, List<FrameData> frames) throws Exception {
        File tempDir = null;
        File outputVideo = null;
        File thumbnailFile = null;

        try {
            Long cctvIdLong = Long.parseLong(cctvIdStr);
            Cctv cctv = cctvRepository.findById(cctvIdLong)
                    .orElseThrow(() -> new IllegalArgumentException("CCTV not found: " + cctvIdStr));

            frames.sort(Comparator.comparingLong(FrameData::getTimestamp));
            LocalDateTime startTime = toLocalTime(frames.get(0).getTimestamp());
            LocalDateTime endTime = toLocalTime(frames.get(frames.size() - 1).getTimestamp());

            tempDir = Files.createTempDirectory("video_" + cctvIdStr + "_").toFile();
            for (int i = 0; i < frames.size(); i++) {
                File dest = new File(tempDir, String.format("frame_%04d.jpg", i));
                Files.write(dest.toPath(), frames.get(i).getImageBytes());
            }

            String baseName = "video_" + cctvIdStr + "_" + System.currentTimeMillis();
            String outputName = baseName + ".mp4";
            String thumbnailName = baseName + ".jpg";

            File videoDir = new File(VIDEO_BASE_PATH + "/" + cctvIdStr);
            File thumbDir = new File(THUMBNAIL_BASE_PATH + "/" + cctvIdStr);
            videoDir.mkdirs();
            thumbDir.mkdirs();

            outputVideo = new File(videoDir, outputName);
            thumbnailFile = new File(thumbDir, thumbnailName);

            Process process = new ProcessBuilder(
                    "ffmpeg", "-framerate", String.valueOf(FRAME_RATE),
                    "-i", new File(tempDir, "frame_%04d.jpg").getAbsolutePath(),
                    "-c:v", "libx264", "-pix_fmt", "yuv420p",
                    outputVideo.getAbsolutePath()
            ).redirectErrorStream(true).start();

            if (process.waitFor() != 0 || !outputVideo.exists()) logProcessError(process);

            Process thumbProcess = new ProcessBuilder(
                    "ffmpeg", "-i", outputVideo.getAbsolutePath(),
                    "-ss", "00:00:01.000", "-vframes", "1",
                    thumbnailFile.getAbsolutePath()
            ).redirectErrorStream(true).start();

            if (thumbProcess.waitFor() != 0 || !thumbnailFile.exists()) logProcessError(thumbProcess);

            // S3 업로드
            String s3VideoPath = "videos/" + cctvIdStr + "/" + outputName;
            String s3ThumbPath = "thumbnails/" + cctvIdStr + "/" + thumbnailName;
            amazonS3.putObject(BUCKET_NAME, s3VideoPath, outputVideo);
            amazonS3.putObject(BUCKET_NAME, s3ThumbPath, thumbnailFile);

            // DB 저장
            videoRepository.save(Video.builder()
                    .cctv(cctv)
                    .s3Path("s3://" + BUCKET_NAME + "/" + s3VideoPath)
                    .thumbnailPath("s3://" + BUCKET_NAME + "/" + s3ThumbPath)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build());

        } finally {
            cleanup(tempDir, outputVideo, thumbnailFile);
        }
    }

    private LocalDateTime toLocalTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    private void logProcessError(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println("[ffmpeg ERROR] " + line);
            }
        }
    }

    private void cleanup(File tempDir, File outputVideo, File thumbnailFile) {
        if (tempDir != null && tempDir.exists()) {
            for (File f : Objects.requireNonNull(tempDir.listFiles())) f.delete();
            tempDir.delete();
        }
        if (outputVideo != null && outputVideo.exists()) outputVideo.delete();
        if (thumbnailFile != null && thumbnailFile.exists()) thumbnailFile.delete();
    }
}
