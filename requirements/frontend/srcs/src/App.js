import React, { useState } from "react";
import axios from "axios";

function App() {
  const [message, setMessage] = useState("");

  const fetchMessage = async () => {
    try {
      const res = await axios.get("/api/test");
      setMessage(res.data.message);
    } catch (err) {
      console.error(err);
      setMessage("API 호출 실패");
    }
  };

  return (
    <div style={{ textAlign: "center", paddingTop: "50px" }}>
      <h1>React ↔ Spring 연결 테스트</h1>
      <button onClick={fetchMessage}>Spring API 호출</button>
      <p>{message}</p>
    </div>
  );
}

export default App;
