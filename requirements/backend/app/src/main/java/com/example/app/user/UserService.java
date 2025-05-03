package com.example.app.user;

import com.example.app.config.JwtUtil;
import com.example.app.user.dto.UserLoginRequestDto;
import com.example.app.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // signup : email, password, name
    public UserResponseDto registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.of(savedUser);
    }

    // login : email, password
    public String login(UserLoginRequestDto requestDto) {
        log.info("[로그인 시도] 이메일: {}", requestDto.getEmail());

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        boolean isMatch = passwordEncoder.matches(requestDto.getPassword(), user.getPassword());
        log.info("[비밀번호 검증] 결과: {}", isMatch);

        if (!isMatch) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("[JWT 발급] 토큰: {}", token);
        return token;
    }

    // check duplicate email
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // me : JWT 토큰 기반 사용자 정보 조회
    public UserResponseDto getMyInfo(String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        String email = jwtUtil.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        return UserResponseDto.of(user);
    }
}
