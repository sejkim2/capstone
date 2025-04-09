package com.example.app.hello;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequestMapping("/api")
public class HelloController {
    
    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("message", "Hello, World!");
    }
}
