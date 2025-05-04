package com.example.app.user;

import com.example.app.user.dto.UserLoginRequestDto;
import com.example.app.user.dto.UserRegisterRequestDto;
import com.example.app.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor    // final 필드에 대한 의존성 주입
public class UserController {

    private final UserService userService;

    // signup : email, password, name
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRegisterRequestDto requestDto) {
        UserResponseDto responseDto = userService.registerUser(
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getName()
        );
        return ResponseEntity.ok(responseDto);  // userid, email, name 반환
    }

    // login : email, password
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto requestDto) {
        String token = userService.login(requestDto);
        return ResponseEntity.ok(token);  // JWT 문자열 직접 반환
    }

    // check duplicate email
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean exists = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(exists);
    }

    // me : JWT 토큰 기반 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@RequestHeader("Authorization") String bearerToken) {
        UserResponseDto userInfo = userService.getMyInfo(bearerToken);
        return ResponseEntity.ok(userInfo);
    }
}
