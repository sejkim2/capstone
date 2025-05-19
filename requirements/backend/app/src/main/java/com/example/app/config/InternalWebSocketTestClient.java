package com.example.app.test;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.net.URI;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;


@ClientEndpoint
// @Component
@Slf4j
public class InternalWebSocketTestClient {

    private Session session;

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://15.164.39.114:8080/ws/stream-view?cctvId=2");
            container.connectToServer(this, uri);
            log.info("📡 WebSocket 클라이언트 연결 시도 중...");
        } catch (Exception e) {
            log.error("❌ WebSocket 클라이언트 연결 실패", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("✅ WebSocket 연결 성공");
        this.session = session;
    }

    @OnMessage
    public void onMessage(byte[] message) {
        log.info("📷 수신된 바이너리 메시지 (크기={} bytes)", message.length);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("❗ WebSocket 에러 발생", throwable);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        log.info("🔌 WebSocket 연결 종료: {}", reason);
    }
}
