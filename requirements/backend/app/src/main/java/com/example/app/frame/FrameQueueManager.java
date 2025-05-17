package com.example.app.frame;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class FrameQueueManager {

    private final BlockingQueue<FrameData> queue = new LinkedBlockingQueue<>(1000); // 최대 1000장까지 버퍼링

    public void enqueue(FrameData frame) {
        if (!queue.offer(frame)) {
            System.out.println("[QUEUE FULL] Dropping frame from CCTV " + frame.getCctvId());
        }
    }

    public FrameData take() throws InterruptedException {
        return queue.take(); // 소비자는 블로킹 방식으로 대기
    }

    public int size() {
        return queue.size();
    }
}
