package com.example.app.frame;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class FrameQueueManager {

    private final Map<String, BlockingQueue<FrameData>> queueMap = new ConcurrentHashMap<>();

    public void enqueue(FrameData frame) {
        String cctvId = frame.getCctvId();
        queueMap.computeIfAbsent(cctvId, k -> new LinkedBlockingQueue<>(1000)).offer(frame);
    }

    public FrameData take(String cctvId) throws InterruptedException {
        return queueMap.computeIfAbsent(cctvId, k -> new LinkedBlockingQueue<>(1000)).take();
    }

    public int size(String cctvId) {
        return queueMap.getOrDefault(cctvId, new LinkedBlockingQueue<>()).size();
    }
}
