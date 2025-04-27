package com.example.app.frame;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FrameRequestDto {
    private List<List<List<Integer>>> frame;  // 3차원 RGB 배열 (Height x Width x 3)
    private String timestamp;                 // (선택) 프레임 타임스탬프
}
