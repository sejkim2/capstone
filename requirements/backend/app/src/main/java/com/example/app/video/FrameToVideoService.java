package com.example.app.video;

import com.amazonaws.services.s3.AmazonS3;
import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class FrameToVideoService {

    private static final String FRAME_BASE_PATH = "/frames";
    private static final int FRAME_THRESHOLD = 500;
    private static final int FRAME_RATE = 15;
    private static final String BUCKET_NAME = "capstone-cctv-bucket";

    private final ReentrantLock lock = new ReentrantLock();
    private final AmazonS3 amazonS3;
    private final CctvRepository cctvRepository;
    private final VideoRepository videoRepository;

    @Scheduled(fixedRate = 10000)
    public void convertFramesToVideo() {
        if (!lock.tryLock()) return;

        try {
            File baseDir = new File(FRAME_BASE_PATH);
            if (!baseDir.exists()) return;

            File[] cctvDirs = baseDir.listFiles(File::isDirectory);
            if (cctvDirs == null) return;

            for (File cctvDir : cctvDirs) {
                File[] frames = cctvDir.listFiles((dir, name) -> name.endsWith(".jpg"));
                if (frames == null || frames.length < FRAME_THRESHOLD) continue;

                Arrays.sort(frames, Comparator.comparing(File::getName));
                processCctvFrames(cctvDir.getName(), frames);
            }

        } finally {
            lock.unlock();
        }
    }

    private void processCctvFrames(String cctvIdStr, File[] frames) {
        File tempDir = null;
        try {
            Long cctvId = Long.parseLong(cctvIdStr);
            Cctv cctv = cctvRepository.findById(cctvId)
                    .orElseThrow(() -> new IllegalArgumentException("CCTV not found: " + cctvId));

            File[] targetFrames = Arrays.copyOfRange(frames, 0, FRAME_THRESHOLD);
            LocalDateTime startTime = extractTimeFromFilename(targetFrames[0]);
            LocalDateTime endTime = extractTimeFromFilename(targetFrames[FRAME_THRESHOLD - 1]);

            tempDir = Files.createTempDirectory("video_" + cctvId + "_").toFile();

            for (int i = 0; i < FRAME_THRESHOLD; i++) {
                File src = targetFrames[i];
                File dest = new File(tempDir, String.format("frame_%04d.jpg", i));
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            String outputName = "video_" + cctvId + "_" + System.currentTimeMillis() + ".mp4";
            File outputVideo = new File("/tmp", outputName);

            Process process = new ProcessBuilder(
                    "ffmpeg", "-framerate", String.valueOf(FRAME_RATE),
                    "-i", new File(tempDir, "frame_%04d.jpg").getAbsolutePath(),
                    "-c:v", "libx264", "-pix_fmt", "yuv420p",
                    outputVideo.getAbsolutePath()
            ).redirectErrorStream(true).start();

            int result = process.waitFor();
            if (result != 0 || !outputVideo.exists()) {
                logProcessError(process);
                throw new RuntimeException("ffmpeg failed with exit code " + result);
            }

            String s3Path = "videos/" + cctvId + "/" + outputName;
            amazonS3.putObject(BUCKET_NAME, s3Path, outputVideo);

            videoRepository.save(Video.builder()
                    .cctv(cctv)
                    .s3Path("s3://" + BUCKET_NAME + "/" + s3Path)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build());

            for (File f : targetFrames) f.delete();

        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        } finally {
            if (tempDir != null && tempDir.exists()) {
                Arrays.stream(Objects.requireNonNull(tempDir.listFiles())).forEach(File::delete);
                tempDir.delete();
            }
        }
    }

    private LocalDateTime extractTimeFromFilename(File file) {
        try {
            String millisStr = file.getName().split("_")[0];
            long millis = Long.parseLong(millisStr);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private void logProcessError(Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            System.err.println("[ffmpeg ERROR OUTPUT]");
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to read ffmpeg process output: " + e.getMessage());
        }
    }
}
