package com.example.app.user;

import com.example.app.user.dto.UserLoginRequestDto;
import com.example.app.user.dto.UserRegisterRequestDto;
import com.example.app.user.dto.UserResponseDto;
import com.example.app.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor    //final injection
public class UserController {

    private final UserService userService;

    //signup : email, password, name
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRegisterRequestDto requestDto) {
        UserResponseDto responseDto = userService.registerUser(
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getName()
        );
        return ResponseEntity.ok(responseDto);  //userid, email, name
    }

    //login : email, password
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        UserResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //check dupulicate email
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean exists = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(exists);
    }
}
