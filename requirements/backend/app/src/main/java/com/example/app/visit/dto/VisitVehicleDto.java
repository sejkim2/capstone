package com.example.app.visit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VisitVehicleDto {
    private String plateNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
}
