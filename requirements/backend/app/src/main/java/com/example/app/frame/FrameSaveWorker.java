package com.example.app.frame;

import com.amazonaws.services.s3.AmazonS3;
import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.video.Video;
import com.example.app.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class FrameSaveWorker implements Runnable {

    private final FrameQueueManager queueManager;
    private final AmazonS3 amazonS3;
    private final CctvRepository cctvRepository;
    private final VideoRepository videoRepository;

    private static final int FRAME_THRESHOLD = 100;
    private static final int FRAME_RATE = 15;
    private static final String FRAME_BASE_PATH = "/frames";
    private static final String VIDEO_BASE_PATH = "/videos";
    private static final String THUMBNAIL_BASE_PATH = "/thumbnails";
    private static final String BUCKET_NAME = "capstone-cctv-bucket";

    // 프레임을 CCTV별로 버퍼링
    private final Map<String, List<FrameData>> bufferMap = new ConcurrentHashMap<>();

    private final Set<String> processingSet = ConcurrentHashMap.newKeySet(); // 중복 방지

@Override
public void run() {
    while (true) {
        try {
            FrameData frame = queueManager.take();
            String cctvId = frame.getCctvId();

            bufferMap.putIfAbsent(cctvId, new ArrayList<>());
            synchronized (bufferMap.get(cctvId)) {
                List<FrameData> buffer = bufferMap.get(cctvId);
                buffer.add(frame);

                // ✅ 프레임 수 카운트 로그
                System.out.println("[FrameSaveWorker] cctvId=" + cctvId + ", 프레임 수=" + buffer.size());

                if (buffer.size() >= FRAME_THRESHOLD && processingSet.add(cctvId)) {
                    List<FrameData> framesToProcess = new ArrayList<>(bufferMap.remove(cctvId));
                    new Thread(() -> {
                        try {
                            processFrames(cctvId, framesToProcess);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            processingSet.remove(cctvId);
                        }
                    }).start();
                }
            }

        } catch (Exception e) {
            System.err.println("[FrameSaveWorker] 오류: " + e.getMessage());
        }
    }
}



//     @Override
// public void run() {
//     System.out.println("[FrameSaveWorker] 워커 실행 시작됨");

//     while (true) {
//         try {
//             FrameData frame = queueManager.take();
//             String cctvId = frame.getCctvId();
//             bufferMap.putIfAbsent(cctvId, new ArrayList<>());
//             bufferMap.get(cctvId).add(frame);

//             int count = bufferMap.get(cctvId).size();
//             System.out.println("[FrameSaveWorker] cctvId=" + cctvId + ", 프레임 수=" + count);

//             if (count >= FRAME_THRESHOLD) {
//                 System.out.println("[FrameSaveWorker] 500장 도달 - 영상 인코딩 시작");
//                 processFrames(cctvId, bufferMap.remove(cctvId));
//             }

//         } catch (Exception e) {
//             System.err.println("[FrameSaveWorker] 오류 발생: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
// }


    // @Override
    // public void run() {
    //     while (true) {
    //         try {
    //             FrameData frame = queueManager.take();
    //             String cctvId = frame.getCctvId();
    //             bufferMap.putIfAbsent(cctvId, new ArrayList<>());
    //             bufferMap.get(cctvId).add(frame);

    //             if (bufferMap.get(cctvId).size() >= FRAME_THRESHOLD) {
    //                 processFrames(cctvId, bufferMap.remove(cctvId));
    //             }

    //         } catch (Exception e) {
    //             System.err.println("[FrameSaveWorker] 오류 발생: " + e.getMessage());
    //             e.printStackTrace();
    //         }
    //     }
    // }

    // private void processFrames(String cctvIdStr, List<FrameData> frames) throws Exception {
    //     File tempDir = null;
    //     File outputVideo = null;
    //     File thumbnailFile = null;

    //     try {
    //         Long cctvId = Long.parseLong(cctvIdStr);
    //         Cctv cctv = cctvRepository.findById(cctvId)
    //                 .orElseThrow(() -> new IllegalArgumentException("CCTV not found: " + cctvId));

    //         frames.sort(Comparator.comparingLong(FrameData::getTimestamp));
    //         LocalDateTime startTime = toLocalTime(frames.get(0).getTimestamp());
    //         LocalDateTime endTime = toLocalTime(frames.get(frames.size() - 1).getTimestamp());

    //         tempDir = Files.createTempDirectory("video_" + cctvId + "_").toFile();
    //         for (int i = 0; i < frames.size(); i++) {
    //             File dest = new File(tempDir, String.format("frame_%04d.jpg", i));
    //             Files.write(dest.toPath(), frames.get(i).getImageBytes());
    //         }

    //         String baseName = "video_" + cctvId + "_" + System.currentTimeMillis();
    //         String outputName = baseName + ".mp4";
    //         String thumbnailName = baseName + ".jpg";

    //         File videoDir = new File(VIDEO_BASE_PATH + "/" + cctvId);
    //         File thumbDir = new File(THUMBNAIL_BASE_PATH + "/" + cctvId);
    //         videoDir.mkdirs();
    //         thumbDir.mkdirs();

    //         outputVideo = new File(videoDir, outputName);
    //         thumbnailFile = new File(thumbDir, thumbnailName);

    //         // FFmpeg로 mp4 생성
    //         Process process = new ProcessBuilder(
    //                 "ffmpeg", "-framerate", String.valueOf(FRAME_RATE),
    //                 "-i", new File(tempDir, "frame_%04d.jpg").getAbsolutePath(),
    //                 "-c:v", "libx264", "-pix_fmt", "yuv420p",
    //                 outputVideo.getAbsolutePath()
    //         ).redirectErrorStream(true).start();
    //         if (process.waitFor() != 0 || !outputVideo.exists()) logProcessError(process);

    //         // 썸네일 생성
    //         Process thumbProcess = new ProcessBuilder(
    //                 "ffmpeg",
    //                 "-i", outputVideo.getAbsolutePath(),
    //                 "-ss", "00:00:01.000",
    //                 "-vframes", "1",
    //                 thumbnailFile.getAbsolutePath()
    //         ).redirectErrorStream(true).start();
    //         if (thumbProcess.waitFor() != 0 || !thumbnailFile.exists()) logProcessError(thumbProcess);

    //         // S3 업로드
    //         String s3VideoPath = "videos/" + cctvId + "/" + outputName;
    //         String s3ThumbPath = "thumbnails/" + cctvId + "/" + thumbnailName;
    //         amazonS3.putObject(BUCKET_NAME, s3VideoPath, outputVideo);
    //         amazonS3.putObject(BUCKET_NAME, s3ThumbPath, thumbnailFile);

    //         // DB 저장
    //         videoRepository.save(Video.builder()
    //                 .cctv(cctv)
    //                 .s3Path("s3://" + BUCKET_NAME + "/" + s3VideoPath)
    //                 .thumbnailPath("s3://" + BUCKET_NAME + "/" + s3ThumbPath)
    //                 .startTime(startTime)
    //                 .endTime(endTime)
    //                 .build());

    //     } finally {
    //         cleanup(tempDir, outputVideo, thumbnailFile);
    //     }
    // }

    private void processFrames(String cctvIdStr, List<FrameData> frames) throws Exception {
    File tempDir = null;
    File outputVideo = null;
    File thumbnailFile = null;

    try {
        System.out.println("[FrameSaveWorker] ▶ 프레임 처리 시작: cctvId=" + cctvIdStr + ", 프레임 수=" + frames.size());

        Long cctvId = Long.parseLong(cctvIdStr);
        Cctv cctv = cctvRepository.findById(cctvId)
                .orElseThrow(() -> new IllegalArgumentException("CCTV not found: " + cctvId));
        System.out.println("[FrameSaveWorker] CCTV 정보 조회 성공");

        frames.sort(Comparator.comparingLong(FrameData::getTimestamp));
        LocalDateTime startTime = toLocalTime(frames.get(0).getTimestamp());
        LocalDateTime endTime = toLocalTime(frames.get(frames.size() - 1).getTimestamp());

        tempDir = Files.createTempDirectory("video_" + cctvId + "_").toFile();
        System.out.println("[FrameSaveWorker] 임시 디렉토리 생성됨: " + tempDir.getAbsolutePath());

        for (int i = 0; i < frames.size(); i++) {
            File dest = new File(tempDir, String.format("frame_%04d.jpg", i));
            Files.write(dest.toPath(), frames.get(i).getImageBytes());
        }
        System.out.println("[FrameSaveWorker] 프레임 JPG 파일 저장 완료");

        String baseName = "video_" + cctvId + "_" + System.currentTimeMillis();
        String outputName = baseName + ".mp4";
        String thumbnailName = baseName + ".jpg";

        File videoDir = new File(VIDEO_BASE_PATH + "/" + cctvId);
        File thumbDir = new File(THUMBNAIL_BASE_PATH + "/" + cctvId);
        videoDir.mkdirs();
        thumbDir.mkdirs();

        outputVideo = new File(videoDir, outputName);
        thumbnailFile = new File(thumbDir, thumbnailName);

        // FFmpeg로 mp4 생성
        System.out.println("[FrameSaveWorker] FFmpeg 영상 인코딩 시작: " + outputVideo.getAbsolutePath());
        Process process = new ProcessBuilder(
                "ffmpeg", "-framerate", String.valueOf(FRAME_RATE),
                "-i", new File(tempDir, "frame_%04d.jpg").getAbsolutePath(),
                "-c:v", "libx264", "-pix_fmt", "yuv420p",
                outputVideo.getAbsolutePath()
        ).redirectErrorStream(true).start();
        if (process.waitFor() != 0 || !outputVideo.exists()) {
            logProcessError(process);
            throw new RuntimeException("FFmpeg 영상 인코딩 실패");
        }
        System.out.println("[FrameSaveWorker] 영상 생성 성공");

        // 썸네일 생성
        System.out.println("[FrameSaveWorker] 썸네일 생성 시작");
        Process thumbProcess = new ProcessBuilder(
                "ffmpeg",
                "-i", outputVideo.getAbsolutePath(),
                "-ss", "00:00:01.000",
                "-vframes", "1",
                thumbnailFile.getAbsolutePath()
        ).redirectErrorStream(true).start();
        if (thumbProcess.waitFor() != 0 || !thumbnailFile.exists()) {
            logProcessError(thumbProcess);
            throw new RuntimeException("FFmpeg 썸네일 생성 실패");
        }
        System.out.println("[FrameSaveWorker] 썸네일 생성 성공");

        // S3 업로드
        String s3VideoPath = "videos/" + cctvId + "/" + outputName;
        String s3ThumbPath = "thumbnails/" + cctvId + "/" + thumbnailName;
        System.out.println("[FrameSaveWorker] S3 업로드 시작");
        amazonS3.putObject(BUCKET_NAME, s3VideoPath, outputVideo);
        amazonS3.putObject(BUCKET_NAME, s3ThumbPath, thumbnailFile);
        System.out.println("[FrameSaveWorker] S3 업로드 완료");

        // DB 저장
        videoRepository.save(Video.builder()
                .cctv(cctv)
                .s3Path("s3://" + BUCKET_NAME + "/" + s3VideoPath)
                .thumbnailPath("s3://" + BUCKET_NAME + "/" + s3ThumbPath)
                .startTime(startTime)
                .endTime(endTime)
                .build());
        System.out.println("[FrameSaveWorker] DB 저장 완료 - startTime=" + startTime + ", endTime=" + endTime);

    } finally {
        cleanup(tempDir, outputVideo, thumbnailFile);
        System.out.println("[FrameSaveWorker] 임시 파일 정리 완료");
    }
}


    private LocalDateTime toLocalTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    private void logProcessError(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            System.err.println("[ffmpeg ERROR]");
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
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
