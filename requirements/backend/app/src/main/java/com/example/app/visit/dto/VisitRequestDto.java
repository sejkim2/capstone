package com.example.app.visit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VisitRequestDto {
    private Long cctvId;
    private String vehicleNumber;
    private LocalDateTime timestamp;
    private String direction; // "in" or "out"
}
