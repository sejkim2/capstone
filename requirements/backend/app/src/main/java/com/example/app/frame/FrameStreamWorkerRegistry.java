package com.example.app.frame;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FrameStreamWorkerRegistry {

    private final Map<String, FrameStreamWorker> workerMap = new ConcurrentHashMap<>();

    public void registerWorker(String cctvId, FrameStreamWorker worker) {
        workerMap.put(cctvId, worker);
    }

    public void registerSession(String cctvId, WebSocketSession session) {
        FrameStreamWorker worker = workerMap.get(cctvId);
        if (worker != null) {
            worker.registerSession(session);
        } else {
            System.err.println("등록되지 않은 CCTV 스트림 요청: " + cctvId);
        }
    }

    public void removeSession(WebSocketSession session) {
        for (FrameStreamWorker worker : workerMap.values()) {
            worker.removeSession(session);
        }
    }
}
