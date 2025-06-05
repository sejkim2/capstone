import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./style.css";

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      const response = await fetch("/api/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });
  
      const data = await response.text(); // ✅ 문자열 그대로 받기
  
      if (!response.ok) {
        alert(`로그인 실패: ${data || "이메일 또는 비밀번호 오류"}`);
        return;
      }
  
      localStorage.setItem("token", data); // ✅ 단순 문자열 저장
      alert("로그인 성공");
      navigate("/main");
    } catch (error) {
      console.error("로그인 에러:", error);
      alert("서버 오류가 발생했습니다.");
    }
  };
  

  const handleSignup = () => {
    navigate("/signup");
  };

  return (
    <div className="login-wrapper">
      <div className="login-container">
        <div className="left-content">
          <div className="title">
            AI를 활용한 <strong>CCTV</strong> 기반<br />고객 관리 플랫폼
          </div>

          <div className="subtitle">Account Log In</div>
          <p className="description">
            PLEASE LOGIN TO CONTINUE TO YOUR ACCOUNT
          </p>

          <input
            type="email"
            className="input-field"
            placeholder="아이디"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            className="input-field"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button className="login-button" onClick={handleLogin}>
            Log in
          </button>

          <div className="links">
            <span>
              No account?{" "}
              <a href="#" onClick={handleSignup}>
                Create one
              </a>
            </span>
          </div>
        </div>

        <div className="right-banner">
          <div className="gradient" />
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
