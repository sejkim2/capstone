// src/screens/LoginPage/index.jsx
import React from "react";
import { SizeLg } from "../../icons/SizeLg";
import { useNavigate } from "react-router-dom";
import "./style.css";

const LoginPage = () => {
  const navigate = useNavigate();

  const handleLogin = () => {
    navigate("/main");
  };

  return (
    <div className="login-wrapper">
      <div className="login-container">
        <div className="left-content">
          <div className="title">AI를 활용한 <strong>CCTV</strong> 기반<br />고객 관리 플랫폼</div>

          <div className="subtitle">Account Log In</div>
          <p className="description">PLEASE LOGIN TO CONTINUE TO YOUR ACCOUNT</p>

          <div className="kakao-button" onClick={handleLogin}>
            <SizeLg className="kakao-icon" />
            <span className="kakao-text">카카오 로그인</span>
          </div>
        </div>

        <div className="right-banner">
          <div className="gradient" />
          <p className="quote">
            <em>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</em><br />
            <strong>- Author’s Name</strong>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
