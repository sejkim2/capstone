package com.example.app.personRecognition.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonRecognitionRequestDto {
    private Long cctvId;
    private String timestamp;
    private String direction;
    private String gender;
    private String ageGroup;
}
