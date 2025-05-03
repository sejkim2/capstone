import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./style.css";

const CreateaccountPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");

  const handleSubmit = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/users/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password, name }),
      });

      const data = await response.json();

      if (response.ok) {
        alert("회원가입 성공!");
        navigate("/login");
      } else {
        alert(`회원가입 실패: ${data.message}`);
      }
    } catch (error) {
      console.error("회원가입 에러:", error);
      alert("서버 오류가 발생했습니다.");
    }
  };

  return (
    <div className="signup-wrapper">
      <div className="signup-form">
        <h2>회원가입</h2>

        <div className="input-group">
          <input
            type="text"
            placeholder="아이디(이메일)"
            className="input-field"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="비밀번호"
            className="input-field"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <input
            type="text"
            placeholder="이름"
            className="input-field"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <button className="submit-button" onClick={handleSubmit}>
          가입하기
        </button>
      </div>
    </div>
  );
};

export default CreateaccountPage;
