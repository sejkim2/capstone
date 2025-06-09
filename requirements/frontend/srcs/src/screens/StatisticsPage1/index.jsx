import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Bar } from "react-chartjs-2";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css";
import { visitorStats } from "../../data/visitorStats";
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
  const getVisitorData = async () => {
    const cutoffDate = new Date("2025-05-21T00:00:00");
    const sDate = new Date(`${startDate}T${startTime}`);
    const eDate = new Date(`${endDate}T${endTime}`);

    // 1. 두 날짜 모두 cutoffDate 이전이면 → 더미만
    if (eDate < cutoffDate) {
      console.log("📦 더미 데이터만 사용");
      return visitorStats.filter((d) => {
        const ts = new Date(d.timestamp);
        return (
          d.cctv === selectedCCTV &&
          ts >= sDate &&
          ts <= eDate
        );
      }).map((d) => ({
        ...d,
        direction: d.direction || "IN",
      }));
    }

    // 2. 두 날짜 모두 cutoffDate 이후이면 → API만
    if (sDate >= cutoffDate) {
      console.log("🌐 API 데이터만 사용");
      const token = localStorage.getItem("token");
      const cctvId = selectedCCTV.replace("CCTV", "");
      const params = new URLSearchParams({
        startDate,
        endDate,
        startTime,
        endTime,
        cctvId,
      });

      const res = await fetch(`/api/person/records?${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return await res.json();
    }

    // 3. 범위가 겹치는 경우 → 더미 + API 병합
    console.log("🔀 더미 + API 데이터 병합");

    // 3-1 더미 필터링
    const dummyFiltered = visitorStats.filter((d) => {
      const ts = new Date(d.timestamp);
      return (
        d.cctv === selectedCCTV &&
        ts >= sDate &&
        ts < cutoffDate
      );
    }).map((d) => ({
      ...d,
      direction: d.direction || "IN",
    }));

    // 3-2 API 호출
    const token = localStorage.getItem("token");
    const cctvId = selectedCCTV.replace("CCTV", "");
    const params = new URLSearchParams({
      startDate: cutoffDate.toISOString().split("T")[0],
      endDate,
      startTime,
      endTime,
      cctvId,
    });

    const res = await fetch(`/api/person/records?${params}`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    const apiData = await res.json();

    // 3-3 병합 후 반환
    return [...dummyFiltered, ...apiData];
  };
  
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
  const [selectedCCTV, setSelectedCCTV] = useState("CCTV2");

  const [showDefaultCharts, setShowDefaultCharts] = useState(true);
  const [monthlyChartData, setMonthlyChartData] = useState(null);
  const [inOutChartData, setInOutChartData] = useState(null);
  const [weekdayChartData, setWeekdayChartData] = useState(null);
  const [hourlyChartData, setHourlyChartData] = useState(null);
  const [genderInOutCharts, setGenderInOutCharts] = useState({ male: null, female: null });

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
    const data = await getVisitorData();
    console.log("📦 기본 차트 데이터:", data);
    const dateRange = getDateRange(startDate, endDate);
    const dailyCounts = {};
    data.filter(d => d.direction === "IN").forEach(({ timestamp }) => {
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
        direction === "IN" ? inPerHour[hour]++ : outPerHour[hour]++;
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
    const data = await getVisitorData();
    console.log("📦 확인 버튼 클릭 시 데이터:", data);
    const maleIn = Array(24).fill(0), maleOut = Array(24).fill(0);
    const femaleIn = Array(24).fill(0), femaleOut = Array(24).fill(0);

    const weekdaySums = Array(7).fill(0), weekdayCounts = Array(7).fill(0);
    const hourSums = Array(24).fill(0), hourCounts = Array(24).fill(0);

    const current = new Date(startDate);
    const end = new Date(endDate);
    while (current <= end) {
      weekdayCounts[current.getDay()]++;
      for (let h = 0; h < 24; h++) hourCounts[h]++;
      current.setDate(current.getDate() + 1);
    }

    data.forEach(({ timestamp, direction, gender }) => {
      const date = new Date(timestamp);
      const day = date.getDay(), hour = date.getHours();
      if (direction === "IN") {
        weekdaySums[day]++;
        hourSums[hour]++;
        gender === "male" ? maleIn[hour]++ : gender === "female" && femaleIn[hour]++;
      } else if (direction === "OUT") {
        gender === "male" ? maleOut[hour]++ : gender === "female" && femaleOut[hour]++;
      }
    });

    const weekdayAverages = weekdaySums.map((sum, i) =>
      weekdayCounts[i] === 0 ? 0 : parseFloat((sum / weekdayCounts[i]).toFixed(2))
    );
    const hourAverages = hourSums.map((sum, i) =>
      hourCounts[i] === 0 ? 0 : parseFloat((sum / hourCounts[i]).toFixed(2))
    );

    const avgMaleIn = maleIn.map((sum, i) =>
      hourCounts[i] === 0 ? 0 : parseFloat((sum / hourCounts[i]).toFixed(2))
    );
    const avgMaleOut = maleOut.map((sum, i) =>
      hourCounts[i] === 0 ? 0 : -parseFloat((sum / hourCounts[i]).toFixed(2))
    );
    const avgFemaleIn = femaleIn.map((sum, i) =>
      hourCounts[i] === 0 ? 0 : parseFloat((sum / hourCounts[i]).toFixed(2))
    );
    const avgFemaleOut = femaleOut.map((sum, i) =>
      hourCounts[i] === 0 ? 0 : -parseFloat((sum / hourCounts[i]).toFixed(2))
    );

    const labels = Array.from({ length: 24 }, (_, i) => `${i}시`);
    setWeekdayChartData({
      labels: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
      datasets: [{ label: "요일별 평균 방문자 수", data: weekdayAverages, backgroundColor: "#5D5FEF", barThickness: 35, borderRadius: 10 }],
    });
    setHourlyChartData({
      labels,
      datasets: [{ label: "시간대별 평균 방문자 수", data: hourAverages, backgroundColor: "#5D5FEF" }],
    });
    setGenderInOutCharts({
      male: {
        labels,
        datasets: [
          { label: "입장", data: avgMaleIn, backgroundColor: "#5D5FEF" },
          { label: "퇴장", data: avgMaleOut, backgroundColor: "#FF8F8F" },
        ],
      },
      female: {
        labels,
        datasets: [
          { label: "입장", data: avgFemaleIn, backgroundColor: "#5D5FEF" },
          { label: "퇴장", data: avgFemaleOut, backgroundColor: "#FF8F8F" },
        ],
      },
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
                <div className="today-sales" style={{ maxHeight: "calc(100vh - 120px)", overflowY: "auto", paddingBottom: "40px", backgroundColor: "#fff", display: "flex", flexDirection: "column", alignItems: "center" }}>
                  {/* 날짜/시간 필터 */}
                  <div style={{ display: "flex", justifyContent: "space-between", width: "80%", marginBottom: "20px" }}>
                    <select onChange={(e) => setSelectedCCTV(e.target.value)} value={selectedCCTV} style={{ padding: "10px", borderRadius: "5px", width: "15%" }}>
                      <option value="CCTV3">매장입구</option>
                      <option value="CCTV2">2층입구</option>
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
                      <div className="chart-container" style={{ width: "60%", marginTop: "20px" }}>
                        <h2>시간대별 평균 증감 추이 (남성)</h2>
                        {genderInOutCharts.male && (
                          <Bar
                            data={genderInOutCharts.male}
                            options={{
                              responsive: true,
                              plugins: { legend: { position: "top" } },
                              scales: {
                                x: { stacked: true },
                                y: {
                                  stacked: true,
                                  beginAtZero: true,
                                  ticks: {
                                    callback: (v) => Number(v).toFixed(3),
                                  },
                                },
                              },
                            }}
                          />
                        )}
                      </div>
                      <div className="chart-container" style={{ width: "60%", marginTop: "20px" }}>
                        <h2>시간대별 평균 증감 추이 (여성)</h2>
                        {genderInOutCharts.female && (
                          <Bar
                            data={genderInOutCharts.female}
                            options={{
                              responsive: true,
                              plugins: { legend: { position: "top" } },
                              scales: {
                                x: { stacked: true },
                                y: {
                                  stacked: true,
                                  beginAtZero: true,
                                  ticks: {
                                    callback: (v) => Number(v).toFixed(3),
                                  },
                                },
                              },
                            }}
                          />
                        )}
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
