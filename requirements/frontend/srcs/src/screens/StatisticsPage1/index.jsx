import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Bar } from "react-chartjs-2";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css";
import  {mockVisitorData}  from "../../data/mockVisitorData";
import {
  defaultInOutData,
  defaultWeekdayChartData,
  defaultHourlyChartData
} from "../../data/defaultChartData";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const VisitorSummaryPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isCCTV = location.pathname === "/cctv";

  const [weekdayChartData, setWeekdayChartData] = useState(null);
  const [hourlyChartData, setHourlyChartData] = useState(null);

  const [startDate, setStartDate] = useState("2025-04-01");
  const [endDate, setEndDate] = useState("2025-04-05");
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("18:00");

  const [selectedCCTV, setSelectedCCTV] = useState("CCTV1");
  const [showDefaultCharts, setShowDefaultCharts] = useState(true);

  const handleDateTimeChange = () => {
    const start = new Date(`${startDate}T${startTime}`);
    const end = new Date(`${endDate}T${endTime}`);

    const filtered = mockVisitorData.filter(({ timestamp }) => {
      const ts = new Date(timestamp);
      return ts >= start && ts <= end;
    });

    const weekdays = Array(7).fill(0);
    const weekdaySums = Array(7).fill(0);
    filtered.forEach(({ timestamp, count }) => {
      const day = new Date(timestamp).getDay();
      weekdays[day]++;
      weekdaySums[day] += count;
    });

    const weekdayAvg = weekdaySums.map((sum, i) =>
      weekdays[i] ? sum / weekdays[i] : 0
    );
    setWeekdayChartData({
      labels: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
      datasets: [{
        label: "요일별 평균 방문자 수",
        data: weekdayAvg,
        backgroundColor: "#5D5FEF",
      }],
    });

    const hours = Array(24).fill(0);
    const hourSums = Array(24).fill(0);
    filtered.forEach(({ timestamp, count }) => {
      const hour = new Date(timestamp).getHours();
      hours[hour]++;
      hourSums[hour] += count;
    });

    const hourAvg = hourSums.map((sum, i) =>
      hours[i] ? sum / hours[i] : 0
    );
    setHourlyChartData({
      labels: Array.from({ length: 24 }, (_, i) => `${i}시`),
      datasets: [{
        label: "시간대별 평균 방문자 수",
        data: hourAvg,
        backgroundColor: "#5D5FEF",
      }],
    });

    setShowDefaultCharts(false); // ✅ 이동
  };

  const monthlyData = {
    labels: Array.from({ length: 30 }, (_, i) => (i + 1).toString().padStart(2, "0")),
    datasets: [{
      label: "월별 방문자 수",
      data: [10, 20, 30, 40, 50, 60, 50, 70, 90, 100, 120, 130, 110, 140, 150, 160, 140, 130, 120, 110, 100, 90, 80, 70, 60, 50, 40, 30, 20, 10],
      backgroundColor: "#5D5FEF",
    }],
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
                    <div className={`frame-2 active`} onClick={() => navigate("/main")} style={{ cursor: "pointer" }}>
                      <img className="icon-outline" alt="Group" src="https://c.animaapp.com/DAdHcKHy/img/group@2x.png" />
                      <div className="text">방문자 통계</div>
                    </div>
                    <div className={`frame-3 ${isCCTV ? "active" : ""}`} onClick={() => navigate("/cctv")} style={{ cursor: "pointer" }}>
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

              <div className="top-bar">
                <div className="div-wrapper">
                  <div className="text-wrapper-4">Visitor Summary</div>
                </div>
              </div>

              <div className="todays-sales">
                <div className="today-sales" style={{
                  height: "850px", display: "flex", flexDirection: "column",
                  justifyContent: "flex-start", alignItems: "center",
                  fontSize: "24px", fontWeight: "500", color: "#444", backgroundColor: "#fff"
                }}>
                  <div className="date-time-picker" style={{ marginBottom: "20px", width: "80%", display: "flex", justifyContent: "space-between" }}>
                    <select onChange={(e) => setSelectedCCTV(e.target.value)} value={selectedCCTV} style={{ padding: "10px", borderRadius: "5px", width: "15%" }}>
                      <option value="CCTV1">CCTV1</option>
                      <option value="CCTV2">CCTV2</option>
                      <option value="CCTV3">CCTV3</option>
                      <option value="CCTV4">CCTV4</option>
                    </select>
                    <div style={{ display: "flex", justifyContent: "space-between", width: "70%" }}>
                      <div>
                        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} style={{ padding: "10px", width: "50%" }} />
                        <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} style={{ padding: "10px", width: "45%" }} />
                      </div>
                      <span style={{ margin: "0 10px", fontSize: "20px", alignSelf: "center" }}>~</span>
                      <div>
                        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} style={{ padding: "10px", width: "50%" }} />
                        <input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} style={{ padding: "10px", width: "45%" }} />
                      </div>
                    </div>
                    <button onClick={handleDateTimeChange} style={{ padding: "10px 20px", borderRadius: "5px", backgroundColor: "#5D5FEF", color: "white", border: "none", fontSize: "16px" }}>
                      확인
                    </button>
                  </div>

                  {showDefaultCharts ? (
                    <>
                      <div className="chart-container" style={{ marginBottom: "20px", width: "60%" }}>
                        <h2>월별 방문자 수</h2>
                        <Bar data={monthlyData} />
                      </div>
                      <div className="chart-container" style={{ width: "60%" }}>
                        <h2>방문자 수 증감 추이</h2>
                        <Bar data={defaultInOutData} options={{
                          responsive: true,
                          plugins: { legend: { position: "top" } },
                          scales: {
                            x: { stacked: true },
                            y: {
                              stacked: true,
                              beginAtZero: true,
                              ticks: {
                                callback: function (value) {
                                  return Math.abs(value);
                                },
                              },
                            },
                          },
                          barThickness: 20,
                          categoryPercentage: 0.6,
                          barPercentage: 0.8,
                        }} />
                      </div>
                    </>
                  ) : (
                    <>
                      {weekdayChartData && (
                        <div className="chart-container" style={{ marginTop: "20px", width: "60%" }}>
                          <h2>요일별 평균 방문자 수</h2>
                          <Bar data={weekdayChartData} />
                        </div>
                      )}
                      {hourlyChartData && (
                        <div className="chart-container" style={{ marginTop: "20px", width: "60%" }}>
                          <h2>시간대별 평균 방문자 수</h2>
                          <Bar data={hourlyChartData} />
                        </div>
                      )}
                    </>
                  )}

                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VisitorSummaryPage;