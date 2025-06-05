package com.example.app.frame;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FrameData {
    private final String cctvId;
    private final byte[] imageBytes;
    private final long timestamp;
}
