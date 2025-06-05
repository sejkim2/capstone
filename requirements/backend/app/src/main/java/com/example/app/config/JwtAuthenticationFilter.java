package com.example.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 로그인, 회원가입, 이메일 중복 확인은 필터 건너뛰기
        if (
            path.equals("/api/users/signup") ||
            path.equals("/api/users/login") ||
            path.equals("/api/users/check-email") ||
            path.equals("/api/person/recognition") ||
            path.equals("/api/visits/vehicle") ||
            path.equals("/cleanup") || 
            path.startsWith("/ws/frame") ||
            path.startsWith("/ws/stream-view")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            setErrorResponse(response, "Authorization header missing or invalid");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            setErrorResponse(response, "Invalid or expired JWT token");
            return;
        }

    String email = jwtUtil.getEmailFromToken(token);

    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(email, null, null);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
}


    //에러 응답 메서드 (JSON)
    private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{ \"error\": \"" + message + "\" }");
        writer.flush();
    }
}
