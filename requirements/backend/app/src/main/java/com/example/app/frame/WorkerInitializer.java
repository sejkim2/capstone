package com.example.app.frame;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerInitializer {

    private final FrameSaveWorker frameSaveWorker;
    private final FrameStreamWorker frameStreamWorker;

    // @PostConstruct
    // public void init() {
    //     Thread saveThread = new Thread(frameSaveWorker, "frame-save-worker");
    //     Thread streamThread = new Thread(frameStreamWorker, "frame-stream-worker");

    //     saveThread.setDaemon(true);   // 데몬 쓰레드로 설정 → Spring 종료 시 같이 종료됨
    //     streamThread.setDaemon(true);

    //     saveThread.start();
    //     streamThread.start();

    //     System.out.println("Frame workers started.");
    // }

    @PostConstruct
public void init() {
    for (int i = 0; i < 4; i++) {
        Thread saveThread = new Thread(frameSaveWorker, "frame-save-worker-" + i);
        saveThread.setDaemon(true);
        saveThread.start();
    }

    Thread streamThread = new Thread(frameStreamWorker, "frame-stream-worker");
    streamThread.setDaemon(true);
    streamThread.start();

    System.out.println("멀티 FrameSaveWorker 시작 완료");
}

}
