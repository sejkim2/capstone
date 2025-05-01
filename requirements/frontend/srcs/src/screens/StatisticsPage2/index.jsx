import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Doughnut } from "react-chartjs-2";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css";
import  {visitorStats}  from "../../data/visitorSatats"; // 배열 형태
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(ArcElement, Tooltip, Legend);

const StatisticsPage2 = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isCCTV = location.pathname === "/cctv";

  const [startDate, setStartDate] = useState("2025-04-01");
  const [endDate, setEndDate] = useState("2025-04-05");
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("18:00");
  const [selectedCCTV, setSelectedCCTV] = useState("CCTV1");

  const [filteredMode, setFilteredMode] = useState(false); // 확인 버튼 눌렀는지 여부
  const [filteredMaleFemale, setFilteredMaleFemale] = useState(null);
  const [filteredAdultChild, setFilteredAdultChild] = useState(null);

  const handleDateTimeChange = () => {
    const start = new Date(`${startDate}T${startTime}`);
    const end = new Date(`${endDate}T${endTime}`);

    const filtered = visitorStats.filter(({ timestamp, cctv }) => {
      const time = new Date(timestamp);
      return time >= start && time <= end && cctv === selectedCCTV;
    });

    const male = filtered.filter(v => v.gender === "male").length;
    const female = filtered.filter(v => v.gender === "female").length;
    const adult = filtered.filter(v => v.ageGroup === "adult").length;
    const child = filtered.filter(v => v.ageGroup === "child").length;

    setFilteredMaleFemale({
      labels: ["남성", "여성"],
      datasets: [
        {
          label: "남성/여성 비율",
          data: [male, female],
          backgroundColor: ["#5D5FEF", "#FF6F61"],
        },
      ],
    });

    setFilteredAdultChild({
      labels: ["성인", "어린이"],
      datasets: [
        {
          label: "성인/어린이 비율",
          data: [adult, child],
          backgroundColor: ["#FFEB3B", "#4CAF50"],
        },
      ],
    });

    setFilteredMode(true);
  };

  const defaultDailyMaleFemaleData = {
    labels: ["남성", "여성"],
    datasets: [
      {
        label: "일간 남성/여성 비율",
        data: [12, 18], // 예시 static
        backgroundColor: ["#5D5FEF", "#FF6F61"],
      },
    ],
  };

  const defaultDailyAdultChildrenData = {
    labels: ["성인", "어린이"],
    datasets: [
      {
        label: "일간 성인/어린이 비율",
        data: [20, 10], // 예시 static
        backgroundColor: ["#FFEB3B", "#4CAF50"],
      },
    ],
  };

  const defaultWeeklyMaleFemaleData = {
    labels: ["남성", "여성"],
    datasets: [
      {
        label: "주간 남성/여성 비율",
        data: [40, 60], // 예시 static
        backgroundColor: ["#5D5FEF", "#FF6F61"],
      },
    ],
  };

  const defaultWeeklyAdultChildrenData = {
    labels: ["성인", "어린이"],
    datasets: [
      {
        label: "주간 성인/어린이 비율",
        data: [55, 45], // 예시 static
        backgroundColor: ["#FFEB3B", "#4CAF50"],
      },
    ],
  };

  return (
    <div className="main-screen">
      <div className="screen">
        <div className="overlap-wrapper">
          <div className="overlap">
            <div className="overlap-group">
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
                        <img
                          className="union"
                          alt="Union"
                          src="https://c.animaapp.com/DAdHcKHy/img/union.svg"
                        />
                      </div>
                      <div className="text-wrapper-2">Sign Out</div>
                    </div>
                  </div>
                  <div className="dummy-logo">
                    <div className="webcam-video-work">
                      <img
                        className="img"
                        alt="Webcam video work"
                        src="https://c.animaapp.com/DAdHcKHy/img/webcam-video--work-video-meeting-camera-company-conference-offic@2x.png"
                      />
                    </div>
                  </div>
                  <div className="text-wrapper-3">Daboa</div>
                </div>
              </div>

              <div className="top-bar">
                <div className="div-wrapper">
                  <div className="text-wrapper-4">Visitor analysis</div>
                </div>
              </div>

              <div className="todays-sales">
                <div
                  className="today-sales"
                  style={{
                    height: "850px",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "flex-start",
                    alignItems: "center",
                    fontSize: "24px",
                    fontWeight: "500",
                    color: "#444",
                    backgroundColor: "#fff",
                  }}
                >
                  {/* 날짜/시간/CCTV 선택 */}
                  <div
                    className="date-time-picker"
                    style={{
                      marginBottom: "20px",
                      width: "80%",
                      display: "flex",
                      justifyContent: "space-between",
                    }}
                  >
                    <select
                      onChange={(e) => setSelectedCCTV(e.target.value)}
                      value={selectedCCTV}
                      style={{ padding: "10px", width: "15%" }}
                    >
                      <option value="CCTV1">CCTV1</option>
                      <option value="CCTV2">CCTV2</option>
                    </select>
                    <div style={{ display: "flex", width: "70%", justifyContent: "space-between" }}>
                      <div>
                        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} style={{ padding: "10px", width: "50%" }} />
                        <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} style={{ padding: "10px", width: "45%" }} />
                      </div>
                      <span style={{ alignSelf: "center" }}>~</span>
                      <div>
                        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} style={{ padding: "10px", width: "50%" }} />
                        <input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} style={{ padding: "10px", width: "45%" }} />
                      </div>
                    </div>
                    <button onClick={handleDateTimeChange} style={{
                      padding: "10px 20px", backgroundColor: "#5D5FEF", color: "white", border: "none", borderRadius: "5px"
                    }}>
                      확인
                    </button>
                  </div>

                  {/* 도넛 차트 */}
                  <div className="donutchart-grid" style={{ display: "flex", flexWrap: "wrap", gap: "40px", justifyContent: "center" }}>
                    {filteredMode ? (
                      <>
                        <div className="donutchart-container" style={{ width: "300px" }}>
                          <h2>남성 / 여성</h2>
                          <Doughnut data={filteredMaleFemale} />
                        </div>
                        <div className="donutchart-container" style={{ width: "300px" }}>
                          <h2>성인 / 어린이</h2>
                          <Doughnut data={filteredAdultChild} />
                        </div>
                      </>
                    ) : (
                      <>
                        <div className="donutchart-container" style={{ width: "350px" }}>
                          <h2>일간 남성/여성 통계</h2>
                          <Doughnut data={defaultDailyMaleFemaleData} />
                        </div>
                        <div className="donutchart-container" style={{ width: "350px" }}>
                          <h2>일간 성인/어린이 통계</h2>
                          <Doughnut data={defaultDailyAdultChildrenData} />
                        </div>
                        <div className="donutchart-container" style={{ width: "350px" }}>
                          <h2>주간 남성/여성 통계</h2>
                          <Doughnut data={defaultWeeklyMaleFemaleData} />
                        </div>
                        <div className="donutchart-container" style={{ width: "350px" }}>
                          <h2>주간 성인/어린이 통계</h2>
                          <Doughnut data={defaultWeeklyAdultChildrenData} />
                        </div>
                      </>
                    )}
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

export default StatisticsPage2;
