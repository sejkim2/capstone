package com.example.app.personRecognition.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonInoutRecordDto {
    private Long cctvId;
    private LocalDateTime timestamp;
    private String direction;
    private String gender;
    private String ageGroup;
}
