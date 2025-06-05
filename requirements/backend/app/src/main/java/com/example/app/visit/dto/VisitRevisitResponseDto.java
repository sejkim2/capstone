package com.example.app.visit.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitRevisitResponseDto {
    private LocalDateTime in;
    private LocalDateTime out;
}
