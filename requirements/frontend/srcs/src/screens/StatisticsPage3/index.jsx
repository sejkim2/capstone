import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  RadialBarChart, RadialBar
} from 'recharts';
import "../MainPage/style.css";
import  avgStayData  from "../../data/avgStayData"; // 배열 형태

const StatisticsPage3 = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isCCTV = location.pathname === "/cctv";

  const [startDate, setStartDate] = useState("2025-04-01");
  const [endDate, setEndDate] = useState("2025-04-05");
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("18:00");
  const [selectedCCTV, setSelectedCCTV] = useState("CCTV1");

  const handleDateTimeChange = () => {
    alert("조회 조건 설정 완료");
  };

  const [dailyAvg, setDailyAvg] = useState(avgStayData.dailyAvg);
  const [weeklyData, setWeeklyData] = useState(avgStayData.weeklyData);

  const maxMinutes = 1440;
  const percentage = (dailyAvg / maxMinutes) * 100;
  const formatTime = (minutes) => `${Math.floor(minutes / 60)}h ${minutes % 60}m`;

  return (
    <div className="main-screen">
      <div className="screen">
        <div className="overlap-wrapper">
          <div className="overlap">
            <div className="overlap-group">
              {/* 사이드 메뉴 */}
              <div className="side-menu">
                <div className="div">
                  <div className="frame">
                    <div
                      className={`frame-2 active`}
                      onClick={() => navigate("/main")}
                      style={{ cursor: "pointer" }}
                    >
                      <img
                        className="icon-outline"
                        alt="Group"
                        src="https://c.animaapp.com/DAdHcKHy/img/group@2x.png"
                      />
                      <div className="text">방문자 통계</div>
                    </div>

                    <div
                      className={`frame-3 ${isCCTV ? "active" : ""}`}
                      onClick={() => navigate("/cctv")}
                      style={{ cursor: "pointer" }}
                    >
                      <IconOutlineShoppingCart1 className="icon-outline" />
                      <div className="text-wrapper">CCTV</div>
                    </div>

                    <div
                      className="frame-4"
                      onClick={() => navigate("/")}
                      style={{ cursor: "pointer" }}
                    >
                      <div className="sign-out-icon">
                        <div className="group">
                          <img
                            className="union"
                            alt="Union"
                            src="https://c.animaapp.com/DAdHcKHy/img/union.svg"
                          />
                        </div>
                      </div>
                      <div className="text-wrapper-2">Sign Out</div>
                    </div>
                  </div>

                  <div className="text-wrapper-3">Daboa</div>
                  <div className="dummy-logo">
                    <div className="webcam-video-work">
                      <img
                        className="img"
                        alt="Webcam video work"
                        src="https://c.animaapp.com/DAdHcKHy/img/webcam-video--work-video-meeting-camera-company-conference-offic@2x.png"
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* 상단 바 */}
              <div className="top-bar">
                <div className="div-wrapper">
                  <div className="text-wrapper-4">Duration of stay</div>
                </div>
              </div>

              {/* 본문 */}
              <div className="todays-sales">
                <div
                  className="today-sales"
                  style={{
                    height: "auto",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "flex-start",
                    alignItems: "center",
                    fontSize: "24px",
                    fontWeight: "500",
                    color: "#444",
                    backgroundColor: "#fff",
                    paddingBottom: "40px",
                  }}
                >
                  {/* 날짜/시간/CCTV 선택 */}
                  <div
                    className="date-time-picker"
                    style={{
                      marginBottom: "30px",
                      width: "80%",
                      display: "flex",
                      justifyContent: "space-between",
                    }}
                  >
                    <select
                      onChange={(e) => setSelectedCCTV(e.target.value)}
                      value={selectedCCTV}
                      style={{
                        padding: "10px",
                        borderRadius: "5px",
                        width: "15%",
                      }}
                    >
                      <option value="CCTV1">CCTV1</option>
                      <option value="CCTV2">CCTV2</option>
                      <option value="CCTV3">CCTV3</option>
                      <option value="CCTV4">CCTV4</option>
                    </select>
                    <div
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        width: "70%",
                      }}
                    >
                      <div>
                        <input
                          type="date"
                          value={startDate}
                          onChange={(e) => setStartDate(e.target.value)}
                          style={{ padding: "10px", width: "50%" }}
                        />
                        <input
                          type="time"
                          value={startTime}
                          onChange={(e) => setStartTime(e.target.value)}
                          style={{ padding: "10px", width: "45%" }}
                        />
                      </div>
                      <span
                        style={{
                          margin: "0 10px",
                          fontSize: "20px",
                          alignSelf: "center",
                        }}
                      >
                        ~
                      </span>
                      <div>
                        <input
                          type="date"
                          value={endDate}
                          onChange={(e) => setEndDate(e.target.value)}
                          style={{ padding: "10px", width: "50%" }}
                        />
                        <input
                          type="time"
                          value={endTime}
                          onChange={(e) => setEndTime(e.target.value)}
                          style={{ padding: "10px", width: "45%" }}
                        />
                      </div>
                    </div>
                    <button
                      onClick={handleDateTimeChange}
                      style={{
                        padding: "10px 20px",
                        borderRadius: "5px",
                        backgroundColor: "#5D5FEF",
                        color: "white",
                        border: "none",
                        fontSize: "16px",
                      }}
                    >
                      확인
                    </button>
                  </div>

                  {/* 📊 그래프 시각화 */}
                  <div style={{
                    width: "90%",
                    backgroundColor: "#fff",
                    padding: "30px",
                    borderRadius: "20px",
                    boxShadow: "0 0 10px rgba(0,0,0,0.1)",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    position: "relative"
                  }}>
                    {/* 일일 평균 체류 시간 (24시간 기준 비율) */}
                    <div style={{
                      textAlign: "center",
                      marginBottom: "40px",
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      position: "relative"
                    }}>
                      <div style={{ fontSize: "20px", fontWeight: "600", marginBottom: "10px" }}>
                        일일 방문자 평균 체류 기간
                      </div>

                      <RadialBarChart
                        width={250}
                        height={250}
                        cx={125}
                        cy={125}
                        innerRadius={80}
                        outerRadius={100}
                        barSize={12}
                        data={[
                          { name: "Background", value: 100, fill: "#EDEDED" },
                          { name: "Stay", value: percentage, fill: "#4A5CFF" },
                        ]}
                        startAngle={90}
                        endAngle={-270}
                      >
                        <RadialBar
                          clockWise
                          dataKey="value"
                          cornerRadius={10}
                        />
                      </RadialBarChart>

                      {/* 중앙 텍스트 */}
                      <div style={{
                        position: "absolute",
                        top: "50%",
                        left: "50%",
                        transform: "translate(-50%, -50%)",
                        fontSize: "20px",
                        fontWeight: "bold",
                        color: "#444"
                      }}>
                        {formatTime(dailyAvg)}
                      </div>
                    </div>

                    <div style={{ width: "100%" }}>
                      <div style={{ fontSize: "20px", fontWeight: "600", marginBottom: "20px" }}>
                        주간 평균 체류시간
                      </div>
                      <ResponsiveContainer width="100%" height={250}>
                        <BarChart data={weeklyData}>
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis dataKey="name" />
                          <YAxis unit="분" />
                          <Tooltip />
                          <Bar dataKey="time" fill="#4A5CFF" radius={[10, 10, 0, 0]} />
                        </BarChart>
                      </ResponsiveContainer>
                    </div>
                  </div>

                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StatisticsPage3;
