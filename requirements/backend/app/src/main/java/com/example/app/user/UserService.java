package com.example.app.user;

import com.example.app.user.dto.UserLoginRequestDto;
import com.example.app.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // signup
    public UserResponseDto registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 암호화!
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)  // 저장은 암호화된 비밀번호로
                .name(name)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.of(savedUser);
    }

    // login
    public UserResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 비밀번호 비교: 평문 vs 암호화된 비밀번호
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserResponseDto.of(user);
    }

    // check duplicate email
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
}
