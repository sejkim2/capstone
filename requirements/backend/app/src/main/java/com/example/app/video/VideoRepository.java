package com.example.app.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByCctvCctvIdAndStartTimeBetween(
        Long cctvId,
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable
    );
}