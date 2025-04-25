package com.example.app.favoriteVideo;

import com.example.app.user.User;
import com.example.app.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteVideoRepository extends JpaRepository<FavoriteVideo, Long> {

    // 특정 사용자 기준 즐겨찾기 영상 목록
    List<FavoriteVideo> findByUser(User user);

    // 특정 영상이 즐겨찾기 되었는지 확인
    Optional<FavoriteVideo> findByUserAndVideo(User user, Video video);

    // 영상 ID 기준 삭제
    void deleteByUserAndVideo(User user, Video video);
}
