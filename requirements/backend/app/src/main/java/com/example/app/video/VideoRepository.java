package com.example.app.video;

import com.example.app.cctv.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // 특정 CCTV에 연결된 모든 영상 조회
    List<Video> findByCctv(Cctv cctv);

    // 또는 cctvId로 조회
    List<Video> findByCctvCctvId(Long cctvId);

    // 특정 시간대에 시작된 영상 조회 (예시)
    List<Video> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
