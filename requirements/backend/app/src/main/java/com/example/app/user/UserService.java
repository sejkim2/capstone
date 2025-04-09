package com.example.app.user;

import com.example.app.user.User;
import com.example.app.user.dto.UserLoginRequestDto;
import com.example.app.user.dto.UserResponseDto;
import com.example.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // signup
    public UserResponseDto registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(email)
                .password(password)  // 추후 암호화 필요
                .name(name)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.of(savedUser);
    }

    // login
    public UserResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserResponseDto.of(user);
    }

    // check dupulicate email
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
}
