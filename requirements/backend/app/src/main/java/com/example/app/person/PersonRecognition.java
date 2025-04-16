package com.example.app.person;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "person_recognition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonRecognition extends AbstractPersonStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recognitionId;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "age_group", nullable = false)
    private String ageGroup;

    @Override
    public String getStatType() {
        return "recognition";
    }
}
