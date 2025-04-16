package com.example.app.person;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "person_inout_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonInoutStats extends AbstractPersonStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statsId;

    @Column(name = "in_count", nullable = false)
    private int inCount;

    @Column(name = "out_count", nullable = false)
    private int outCount;

    @Override
    public String getStatType() {
        return "inout";
    }
}
