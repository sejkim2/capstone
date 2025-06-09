import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Doughnut } from "react-chartjs-2";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css";
import { visitorStats } from "../../data/visitorStats";
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

  const today = new Date();
  const weekAgo = new Date();
  weekAgo.setDate(today.getDate() - 7);

  const [startDate, setStartDate] = useState(weekAgo.toISOString().split("T")[0]);
  const [endDate, setEndDate] = useState(today.toISOString().split("T")[0]);
  const [startTime, setStartTime] = useState("00:00");
  const [endTime, setEndTime] = useState("23:59");
  const [selectedCCTV, setSelectedCCTV] = useState("CCTV2");

  const [filteredMode, setFilteredMode] = useState(false);
  const [dailyMaleFemale, setDailyMaleFemale] = useState(null);
  const [dailyAdultChild, setDailyAdultChild] = useState(null);
  const [weeklyMaleFemale, setWeeklyMaleFemale] = useState(null);
  const [weeklyAdultChild, setWeeklyAdultChild] = useState(null);
  const [filteredMaleFemale, setFilteredMaleFemale] = useState(null);
  const [filteredAdultChild, setFilteredAdultChild] = useState(null);

  const filterData = (list, groupField, values) => {
    return values.map(value =>
      list.filter(d => d.direction === "IN" && d[groupField] === value).length
    );
  };

  const fetchStats = async () => {
    const token = localStorage.getItem("token");
    const cctvId = selectedCCTV.replace("CCTV", "");
    console.log("🛠️ 선택된 CCTV ID (일간/주간 통계용):", cctvId); // ✅ 로그 추가

    const fetchData = async (startDate, endDate) => {
      const params = new URLSearchParams({
        cctvId,
        startDate,
        endDate,
        startTime: "00:00",
        endTime: "23:59",
      });
      const res = await fetch(`/api/person/records?${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return res.json();
    };

    const todayStr = new Date().toISOString().split("T")[0];
    const weekAgoStr = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split("T")[0];

    const todayData = await fetchData(todayStr, todayStr);
    console.log("✅ 오늘 데이터:", todayData); // ✅ 로그 추가

    const weekData = await fetchData(weekAgoStr, todayStr);
    console.log("📅 지난 주 데이터:", weekData); // ✅ 로그 추가

    setDailyMaleFemale({
      labels: ["남성", "여성"],
      datasets: [{
        label: "일간 남성/여성",
        data: filterData(todayData, "gender", ["male", "female"]),
        backgroundColor: ["#6FA8DC", "#F08080"],
      }],
    });

    setDailyAdultChild({
      labels: ["어린이", "성인", "노인"],
      datasets: [{
        label: "일간 연령대",
        data: filterData(todayData, "ageGroup", ["less18", "middle", "over60"]),
        backgroundColor: ["#FFD54F", "#A8E6CF", "#D7CCC8"],
      }],
    });

    setWeeklyMaleFemale({
      labels: ["남성", "여성"],
      datasets: [{
        label: "주간 남성/여성",
        data: filterData(weekData, "gender", ["male", "female"]),
        backgroundColor: ["#6FA8DC", "#F08080"],
      }],
    });

    setWeeklyAdultChild({
      labels: ["어린이", "성인", "노인"],
      datasets: [{
        label: "주간 연령대",
        data: filterData(weekData, "ageGroup", ["less18", "middle", "over60"]),
        backgroundColor: ["#FFD54F", "#A8E6CF", "#D7CCC8"],
      }],
    });
  };

  const handleDateTimeChange = async () => {
    const token = localStorage.getItem("token");
    const cctvId = selectedCCTV.replace("CCTV", "");

    const params = new URLSearchParams({
      cctvId,
      startDate,
      endDate,
      startTime,
      endTime,
    });

    const res = await fetch(`/api/person/records?${params}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const apiData = await res.json();

    // 🔵 더미 + API 병합 처리
    const sDate = new Date(`${startDate}T${startTime}`);
    const eDate = new Date(`${endDate}T${endTime}`);
    const dummyCutoff = new Date("2025-05-21T00:00:00");

    const dummyData = visitorStats.filter((d) => {
      const ts = new Date(d.timestamp);
      return (
        d.cctv === selectedCCTV &&
        ts >= sDate &&
        ts <= eDate
      );
    });

    const combinedData = eDate < dummyCutoff
      ? dummyData
      : sDate >= dummyCutoff
        ? apiData
        : [...dummyData, ...apiData];

    console.log("📦 병합된 통계 데이터:", combinedData);

    setFilteredMaleFemale({
      labels: ["남성", "여성"],
      datasets: [{
        label: "기간 내 남성/여성",
        data: filterData(combinedData, "gender", ["male", "female"]),
        backgroundColor: ["#6FA8DC", "#F08080"],
      }],
    });

    setFilteredAdultChild({
      labels: ["어린이", "성인", "노인"],
      datasets: [{
        label: "기간 내 연령대",
        data: filterData(combinedData, "ageGroup", ["less18", "middle", "over60"]),
        backgroundColor: ["#FFD54F", "#A8E6CF", "#D7CCC8"],
      }],
    });

    setFilteredMode(true);
  };

  useEffect(() => {
    fetchStats();
  }, [selectedCCTV]);

  const renderChart = (data, title) => (
    <div
      className="donutchart-container"
      style={{
        width: "350px",
        height: "340px", // ✅ 고정된 높이
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#fff",
        padding: "10px",
        boxShadow: "0 0 5px rgba(0,0,0,0.1)",
        borderRadius: "10px"
      }}
    >
      <h2 style={{ fontSize: "16px", marginBottom: "10px" }}>{title}</h2>
      {data?.datasets?.[0]?.data?.some((n) => n > 0) ? (
        <Doughnut
          data={data}
          options={{
            animation: false,
            maintainAspectRatio: false,
            responsive: true,
            plugins: {
              legend: { position: "bottom" },
            },
          }}
          height={220}
        />
      ) : (
        <div
          style={{
            height: "220px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: "#aaa",
          }}
        >
          데이터 없음
        </div>
      )}
    </div>
  );

  return (
    <div className="main-screen">
      <div className="screen">
        <div className="overlap-wrapper">
          <div className="overlap">
            <div className="overlap-group">
              <div className="side-menu">
                <div className="div">
                  <div className="frame">
                    <div className="frame-2 active" onClick={() => navigate("/main")} style={{ cursor: "pointer" }}>
                      <img className="icon-outline" alt="Group" src="https://c.animaapp.com/DAdHcKHy/img/group@2x.png" />
                      <div className="text">방문자 통계</div>
                    </div>
                    <div className={`frame-3 ${isCCTV ? "active" : ""}`} onClick={() => navigate("/cctv")} style={{ cursor: "pointer" }}>
                      <IconOutlineShoppingCart1 className="icon-outline" />
                      <div className="text-wrapper">CCTV</div>
                    </div>
                    <div className="frame-4" onClick={() => navigate("/")} style={{ cursor: "pointer" }}>
                      <div className="sign-out-icon">
                        <img className="union" alt="Union" src="https://c.animaapp.com/DAdHcKHy/img/union.svg" />
                      </div>
                      <div className="text-wrapper-2">Sign Out</div>
                    </div>
                  </div>
                  <div className="dummy-logo">
                    <div className="webcam-video-work">
                      <img className="img" alt="Webcam video work" src="https://c.animaapp.com/DAdHcKHy/img/webcam-video--work-video-meeting-camera-company-conference-offic@2x.png" />
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
                <div className="today-sales" style={{
                  height: "850px",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "flex-start",
                  alignItems: "center",
                  fontSize: "24px",
                  fontWeight: "500",
                  color: "#444",
                  backgroundColor: "#fff",
                }}>
                  <div className="date-time-picker" style={{ marginBottom: "20px", width: "80%", display: "flex", justifyContent: "space-between" }}>
                    <select onChange={(e) => setSelectedCCTV(e.target.value)} value={selectedCCTV} style={{ padding: "10px", width: "15%" }}>
                      <option value="CCTV3">매장입구</option>
                      <option value="CCTV2">2층입구</option>
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
                      padding: "10px 20px",
                      backgroundColor: "#5D5FEF",
                      color: "white",
                      border: "none",
                      borderRadius: "5px"
                    }}>
                      확인
                    </button>
                  </div>

                  <div className="donutchart-grid" style={{ display: "flex", flexWrap: "wrap", gap: "40px", justifyContent: "center" }}>
                    {filteredMode ? (
                      <>
                        {renderChart(filteredMaleFemale, "기간 내 남성/여성")}
                        {renderChart(filteredAdultChild, "기간 내 성인/어린이")}
                      </>
                    ) : (
                      <>
                        {renderChart(dailyMaleFemale, "일간 남성/여성")}
                        {renderChart(dailyAdultChild, "일간 성인/어린이")}
                        {renderChart(weeklyMaleFemale, "주간 남성/여성")}
                        {renderChart(weeklyAdultChild, "주간 성인/어린이")}
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
