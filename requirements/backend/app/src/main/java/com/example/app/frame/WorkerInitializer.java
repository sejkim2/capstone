package com.example.app.frame;

import com.amazonaws.services.s3.AmazonS3;
import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.video.VideoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerInitializer {

    private final FrameQueueManager queueManager;
    private final AmazonS3 amazonS3;
    private final CctvRepository cctvRepository;
    private final VideoRepository videoRepository;
    private final FrameStreamWorkerRegistry streamRegistry;

    @PostConstruct
    public void init() {
        for (Cctv cctv : cctvRepository.findAll()) {
            // String cctvId = cctv.getId().toString();
            String cctvId = cctv.getCctvId().toString();


            // 저장용 워커 실행
            FrameSaveWorker saveWorker = new FrameSaveWorker(
                    cctvId, queueManager, amazonS3, cctvRepository, videoRepository
            );
            new Thread(saveWorker, "save-" + cctvId).start();

            // 스트리밍용 워커 실행
            FrameStreamWorker streamWorker = new FrameStreamWorker(cctvId, queueManager);
            streamRegistry.registerWorker(cctvId, streamWorker);
            new Thread(streamWorker, "stream-" + cctvId).start();
        }
    }
}

