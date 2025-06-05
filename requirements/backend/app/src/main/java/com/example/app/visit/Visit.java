package com.example.app.visit;

import com.example.app.cctv.Cctv;
import com.example.app.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Visit {

    @Id //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id 자동 생성
    @Column(name = "visit_id")  //칼럼명 : visit_id
    private Long visitId;

    @ManyToOne(fetch = FetchType.LAZY)  //visit : cctv = N : 1, 성능 최적화를 위해 FetchType.LAZY
    @JoinColumn(name = "cctv_id", nullable = false) //cctv_id를 가리키는 외래키
    private Cctv cctv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
