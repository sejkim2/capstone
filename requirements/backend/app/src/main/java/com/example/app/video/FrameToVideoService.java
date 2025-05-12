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
    System.out.println("[SCHEDULED] convertFramesToVideo() 호출됨");

    if (!lock.tryLock()) {
        System.out.println("[INFO] 이전 작업 진행 중 → 스킵");
        return;
    }

    try {
        File baseDir = new File(FRAME_BASE_PATH);
        if (!baseDir.exists()) {
            System.out.println("[WARN] /frames 디렉토리 없음");
            return;
        }

        File[] cctvDirs = baseDir.listFiles(File::isDirectory);
        System.out.println("[INFO] CCTV 디렉토리 개수: " + (cctvDirs != null ? cctvDirs.length : 0));

        for (File cctvDir : cctvDirs) {
            File[] frames = cctvDir.listFiles((dir, name) -> name.endsWith(".jpg"));
            if (frames == null || frames.length < FRAME_THRESHOLD) {
                System.out.println("[INFO] CCTV " + cctvDir.getName() + ": insufficient frames (" + (frames != null ? frames.length : 0) + ")");
                continue;
            }

            System.out.println("[INFO] CCTV " + cctvDir.getName() + ": 충분한 프레임 감지됨 → 영상 변환 시작");
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

        tempDir = new File("/tmp/video_" + UUID.randomUUID());
        tempDir.mkdirs();

        for (int i = 0; i < FRAME_THRESHOLD; i++) {
            File src = targetFrames[i];
            File dest = new File(tempDir, String.format("frame_%04d.jpg", i));
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        String outputName = "video_" + cctvId + "_" + System.currentTimeMillis() + ".mp4";
        File outputVideo = new File("/tmp/" + outputName);

        // 1. ffmpeg 실행 로그
        System.out.println("[INFO] Starting ffmpeg: " + outputVideo.getAbsolutePath());

        Process process = new ProcessBuilder(
                "ffmpeg", "-framerate", String.valueOf(FRAME_RATE),
                "-i", new File(tempDir, "frame_%04d.jpg").getAbsolutePath(),
                "-c:v", "libx264", "-pix_fmt", "yuv420p",
                outputVideo.getAbsolutePath()
        ).redirectErrorStream(true).start();

        int result = process.waitFor();
        if (result != 0 || !outputVideo.exists()) {
            System.err.println("[ERROR] ffmpeg failed with exit code " + result);
            logProcessError(process);
            throw new RuntimeException("ffmpeg failed");
        }

        // 2. 동영상 생성 성공 로그
        System.out.println("[INFO] Video created: " + outputVideo.getAbsolutePath());

        // 3. S3 업로드 로그
        String s3Path = "videos/" + cctvId + "/" + outputName;
        System.out.println("[INFO] Uploading to S3: s3://" + BUCKET_NAME + "/" + s3Path);

        amazonS3.putObject(BUCKET_NAME, s3Path, outputVideo);
        System.out.println("[INFO] S3 upload complete: s3://" + BUCKET_NAME + "/" + s3Path);

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
            String millisStr = file.getName().split("_")[0]; // 예: 1715501234567_UUID.jpg
            long millis = Long.parseLong(millisStr);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private void deleteDirectoryIfExists(String prefix) {
        File tmpDir = new File("/tmp");
        File[] dirs = tmpDir.listFiles((dir, name) -> name.startsWith(prefix));
        if (dirs != null) {
            for (File dir : dirs) {
                for (File f : Objects.requireNonNull(dir.listFiles())) f.delete();
                dir.delete();
            }
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
