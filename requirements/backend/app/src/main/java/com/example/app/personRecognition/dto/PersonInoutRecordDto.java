package com.example.app.personRecognition.dto;

import java.time.LocalDateTime;

public class PersonInoutRecordDto {

    private Long cctvId;
    private LocalDateTime timestamp;
    private String direction;
    private String gender;
    private String ageGroup;

    public PersonInoutRecordDto(Long cctvId, LocalDateTime timestamp, String direction, String gender, String ageGroup) {
        this.cctvId = cctvId;
        this.timestamp = timestamp;
        this.direction = direction;
        this.gender = gender;
        this.ageGroup = ageGroup;
    }

    // Getters (or use Lombok @Getter)
    public Long getCctvId() {
        return cctvId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDirection() {
        return direction;
    }

    public String getGender() {
        return gender;
    }

    public String getAgeGroup() {
        return ageGroup;
    }
}
