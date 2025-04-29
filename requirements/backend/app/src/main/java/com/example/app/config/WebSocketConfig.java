package com.example.app.config;

import com.example.app.frame.FrameWebSocketHandler;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final FrameWebSocketHandler frameWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(frameWebSocketHandler, "/ws/frame")
                .setAllowedOrigins("*"); // 개발용 허용
    }

    // WebSocket 버퍼 크기 300KB 설정
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(300 * 1024); // 300KB
        container.setMaxTextMessageBufferSize(300 * 1024);   // 300KB
        return container;
    }
}
