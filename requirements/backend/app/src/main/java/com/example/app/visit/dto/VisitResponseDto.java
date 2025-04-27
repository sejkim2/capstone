package com.example.app.visit.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitResponseDto {
    private String vehicleId;
    private LocalDateTime in;
    private LocalDateTime out;
    private long durationMinutes;
}
