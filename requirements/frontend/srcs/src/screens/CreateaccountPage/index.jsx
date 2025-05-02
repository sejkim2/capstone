import React from "react";
import { useNavigate } from "react-router-dom";
import "./style.css";

const CreateaccountPage = () => {
  const navigate = useNavigate();

  const handleSubmit = () => {
    // 회원가입 로직 후 로그인 페이지로 이동
    navigate("/login");
  };

  return (
    <div className="signup-wrapper">
      <div className="signup-form">
        <h2>회원가입</h2>

        <div className="input-group">
          <input type="text" placeholder="아이디(이메일)" className="input-field" />
          <input type="password" placeholder="비밀번호" className="input-field" />
          <input type="text" placeholder="이름" className="input-field" />
        </div>

        <button className="submit-button" onClick={handleSubmit}>
          가입하기
        </button>
      </div>
    </div>
  );
};

export default CreateaccountPage;
