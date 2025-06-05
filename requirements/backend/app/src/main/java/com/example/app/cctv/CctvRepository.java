package com.example.app.cctv;

import com.example.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CctvRepository extends JpaRepository<Cctv, Long> {

    // 특정 사용자(user)가 소유한 모든 CCTV 조회
    List<Cctv> findByUser(User user);

    // 또는 userId로도 조회 가능
    List<Cctv> findByUserUserId(Long userId);
}
