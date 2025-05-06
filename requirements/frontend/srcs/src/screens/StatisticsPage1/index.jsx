import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Bar } from "react-chartjs-2";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css";

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

  const today = new Date();
  const thirtyDaysAgo = new Date(today);
  thirtyDaysAgo.setDate(today.getDate() - 30);

  const [startDate, setStartDate] = useState(thirtyDaysAgo.toISOString().split("T")[0]);
  const [endDate, setEndDate] = useState(today.toISOString().split("T")[0]);
  const [startTime, setStartTime] = useState("00:00");
  const [endTime, setEndTime] = useState("23:59");
  const [selectedCCTV, setSelectedCCTV] = useState("CCTV1");

  const [showDefaultCharts, setShowDefaultCharts] = useState(true);
  const [monthlyChartData, setMonthlyChartData] = useState(null);
  const [inOutChartData, setInOutChartData] = useState(null);
  const [weekdayChartData, setWeekdayChartData] = useState(null);
  const [hourlyChartData, setHourlyChartData] = useState(null);

  const getDateRange = (start, end) => {
    const result = [];
    const cur = new Date(start);
    const last = new Date(end);
    while (cur <= last) {
      result.push(cur.toISOString().split("T")[0]);
      cur.setDate(cur.getDate() + 1);
    }
    return result;
  };

  const fetchAndSetDefaultCharts = async () => {
    const token = localStorage.getItem("token");
    const cctvId = selectedCCTV.replace("CCTV", "");
    const params = new URLSearchParams({
      startDate,
      endDate,
      startTime: "00:00",
      endTime: "23:59",
      cctvId,
    });

    const res = await fetch(`http://localhost:8080/api/person/records?${params}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const data = await res.json();

    const dateRange = getDateRange(startDate, endDate);
    const dailyCounts = {};
    data.filter(d => d.direction === "in").forEach(({ timestamp }) => {
      const dateKey = new Date(timestamp).toISOString().split("T")[0];
      dailyCounts[dateKey] = (dailyCounts[dateKey] || 0) + 1;
    });
    const dailyData = dateRange.map(date => dailyCounts[date] || 0);
    setMonthlyChartData({
      labels: dateRange,
      datasets: [{ label: "일별 방문자 수", data: dailyData, backgroundColor: "#5D5FEF" }],
    });

    const inPerHour = Array(24).fill(0);
    const outPerHour = Array(24).fill(0);
    const todayStr = new Date().toISOString().split("T")[0];

    data.forEach(({ timestamp, direction }) => {
      const date = new Date(timestamp);
      const dateStr = date.toISOString().split("T")[0];
      const hour = date.getHours();
      if (dateStr === todayStr) {
        direction === "in" ? inPerHour[hour]++ : outPerHour[hour]++;
      }
    });
    setInOutChartData({
      labels: Array.from({ length: 24 }, (_, i) => `${i}시`),
      datasets: [
        { label: "입장", data: inPerHour, backgroundColor: "#5D5FEF" },
        { label: "퇴장", data: outPerHour.map(v => -v), backgroundColor: "#FF8F8F" },
      ],
    });
  };

  const handleDateTimeChange = async () => {
    const token = localStorage.getItem("token");
    const cctvId = selectedCCTV.replace("CCTV", "");
    const params = new URLSearchParams({
      startDate,
      endDate,
      startTime,
      endTime,
      cctvId,
    });

    const res = await fetch(`http://localhost:8080/api/person/records?${params}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const data = await res.json();

    const weekdaySums = Array(7).fill(0);
    const weekdayCounts = Array(7).fill(0);
    const hourSums = Array(24).fill(0);
    const hourCounts = Array(24).fill(0);

    const current = new Date(startDate);
    const end = new Date(endDate);
    while (current <= end) {
      weekdayCounts[current.getDay()]++;
      for (let h = 0; h < 24; h++) hourCounts[h]++;
      current.setDate(current.getDate() + 1);
    }

    data.forEach(({ timestamp, direction }) => {
      if (direction === "in") {
        const date = new Date(timestamp);
        const day = date.getDay();
        const hour = date.getHours();
        weekdaySums[day]++;
        hourSums[hour]++;
      }
    });

    const weekdayAverages = weekdaySums.map((sum, i) =>
      weekdayCounts[i] === 0 ? 0 : parseFloat((sum / weekdayCounts[i]).toFixed(2))
    );

    const hourAverages = hourSums.map((sum, i) =>
      hourCounts[i] === 0 ? 0 : parseFloat((sum / hourCounts[i]).toFixed(2))
    );

    setWeekdayChartData({
      labels: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
      datasets: [{ label: "요일별 평균 방문자 수", data: weekdayAverages, backgroundColor: "#5D5FEF" }],
    });

    setHourlyChartData({
      labels: Array.from({ length: 24 }, (_, i) => `${i}시`),
      datasets: [{ label: "시간대별 평균 방문자 수", data: hourAverages, backgroundColor: "#5D5FEF" }],
    });

    setShowDefaultCharts(false);
  };

  useEffect(() => {
    fetchAndSetDefaultCharts();
  }, [selectedCCTV]);

  return (
    <div className="main-screen">
      <div className="screen">
        <div className="overlap-wrapper">
          <div className="overlap">
            <div className="overlap-group">

              {/* 사이드바 */}
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
                    <div className="frame-4" onClick={() => navigate("/")} style={{ cursor: "pointer" }}>
                      <div className="sign-out-icon">
                        <div className="group">
                          <img className="union" alt="Union" src="https://c.animaapp.com/DAdHcKHy/img/union.svg" />
                        </div>
                      </div>
                      <div className="text-wrapper-2">Sign Out</div>
                    </div>
                  </div>
                  <div className="text-wrapper-3">Daboa</div>
                  <div className="dummy-logo">
                    <div className="webcam-video-work">
                      <img className="img" alt="Webcam" src="https://c.animaapp.com/DAdHcKHy/img/webcam-video--work-video-meeting-camera-company-conference-offic@2x.png" />
                    </div>
                  </div>
                </div>
              </div>

              {/* 상단 바 */}
              <div className="top-bar">
                <div className="div-wrapper">
                  <div className="text-wrapper-4">Visitor Summary</div>
                </div>
              </div>

              {/* 메인 콘텐츠 */}
              <div className="todays-sales">
                <div className="today-sales" style={{
                  height: "850px", display: "flex", flexDirection: "column",
                  alignItems: "center", fontSize: "24px", fontWeight: "500", color: "#444", backgroundColor: "#fff"
                }}>

                  {/* 날짜/시간 필터 */}
                  <div style={{ display: "flex", justifyContent: "space-between", width: "80%", marginBottom: "20px" }}>
                    <select onChange={(e) => setSelectedCCTV(e.target.value)} value={selectedCCTV} style={{ padding: "10px", borderRadius: "5px", width: "15%" }}>
                      <option value="CCTV1">CCTV1</option>
                      <option value="CCTV2">CCTV2</option>
                      <option value="CCTV3">CCTV3</option>
                      <option value="CCTV4">CCTV4</option>
                    </select>
                    <div style={{ display: "flex", justifyContent: "space-between", width: "65%" }}>
                      <div>
                        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} style={{ padding: "10px", width: "48%" }} />
                        <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} style={{ padding: "10px", width: "48%" }} />
                      </div>
                      <span style={{ alignSelf: "center", margin: "0 10px" }}>~</span>
                      <div>
                        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} style={{ padding: "10px", width: "48%" }} />
                        <input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} style={{ padding: "10px", width: "48%" }} />
                      </div>
                    </div>
                    <button onClick={handleDateTimeChange} style={{ padding: "10px 20px", backgroundColor: "#5D5FEF", color: "white", border: "none", borderRadius: "5px" }}>
                      확인
                    </button>
                  </div>

                  {/* 조건부 차트 출력 */}
                  {showDefaultCharts ? (
                    <>
                      <div className="chart-container" style={{ marginBottom: "20px", width: "60%" }}>
                        <h2>일별 방문자 수</h2>
                        {monthlyChartData && <Bar data={monthlyChartData} />}
                      </div>
                      <div className="chart-container" style={{ width: "60%" }}>
                        <h2>방문자 수 증감 추이</h2>
                        {inOutChartData && (
                          <Bar
                            data={inOutChartData}
                            options={{
                              responsive: true,
                              plugins: { legend: { position: "top" } },
                              scales: {
                                x: { stacked: true },
                                y: {
                                  stacked: true,
                                  beginAtZero: true,
                                  ticks: { callback: (v) => Math.abs(v) },
                                },
                              },
                            }}
                          />
                        )}
                      </div>
                    </>
                  ) : (
                    <>
                      <div className="chart-container" style={{ marginBottom: "20px", width: "60%" }}>
                        <h2>요일별 평균 방문자 수</h2>
                        {weekdayChartData && <Bar data={weekdayChartData} />}
                      </div>
                      <div className="chart-container" style={{ width: "60%" }}>
                        <h2>시간대별 평균 방문자 수</h2>
                        {hourlyChartData && <Bar data={hourlyChartData} />}
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
  );
};

export default VisitorSummaryPage;
