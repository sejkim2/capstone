import React from "react";
import { useNavigate } from "react-router-dom";
import "./style.css";

const LoginPage = () => {
  const navigate = useNavigate();

  const handleLogin = () => {
    navigate("/main");
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

          <input type="email" className="input-field" placeholder="아이디" />
          <input type="password" className="input-field" placeholder="비밀번호" />
          <button className="login-button" onClick={handleLogin}>
            Log in
          </button>

          <div className="links">
            <span>
              No account?{" "}
              <a href="#" onClick={handleSignup}>Create one</a>
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
