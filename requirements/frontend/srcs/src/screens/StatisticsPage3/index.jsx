import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, RadialBarChart, RadialBar, ReferenceLine
} from "recharts";
import "../MainPage/style.css";

const StatisticsPage3 = () => {
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

  const [dailyAvg, setDailyAvg] = useState(0);
  const [weeklyData, setWeeklyData] = useState([]);
  const [dailyChartData, setDailyChartData] = useState([]);
  const [noData, setNoData] = useState(false);
  const [isConfirmed, setIsConfirmed] = useState(false);

  const maxMinutes = 1440;
  const safeDailyAvg = Math.min(Math.max(dailyAvg, 0), maxMinutes);
  const percentage = (safeDailyAvg / maxMinutes) * 100;
  const overallAvg = weeklyData.length
    ? Math.round(weeklyData.reduce((sum, d) => sum + d.time, 0) / weeklyData.length)
    : 0;

  const formatTime = (minutes) => {
    if (isNaN(minutes)) return "0h 0m";
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${h}h ${m}m`;
  };

  const getCctvId = (name) => {
    const mapping = { CCTV1: 1, CCTV2: 2, CCTV3: 3, CCTV4: 4 };
    return mapping[name] || 1;
  };

  const getDateRange = (start, end) => {
    const dates = [];
    let current = new Date(start);
    const last = new Date(end);
    while (current <= last) {
      dates.push(current.toISOString().split("T")[0]);
      current.setDate(current.getDate() + 1);
    }
    return dates;
  };

  const fetchInitialStayData = async () => {
    const token = localStorage.getItem("token");
    const cctvId = getCctvId(selectedCCTV);
    const params = new URLSearchParams({
      startDate,
      endDate,
      startTime,
      endTime,
      cctvId,
    });

    try {
      const res = await fetch(`http://localhost:8080/api/visits?${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      const visitList = data.visits || [];

      if (visitList.length === 0) return;

      const dailyMap = {};
      visitList.forEach(v => {
        const date = new Date(v.in).toISOString().split("T")[0];
        if (!dailyMap[date]) dailyMap[date] = [];
        dailyMap[date].push(v.durationMinutes);
      });
      const dailyAverages = Object.values(dailyMap).map(list =>
        list.length ? list.reduce((a, b) => a + b, 0) / list.length : 0
      );
      const avg = dailyAverages.length
        ? Math.round(dailyAverages.reduce((a, b) => a + b, 0) / dailyAverages.length)
        : 0;
      setDailyAvg(avg);

      const dayMap = {};
      visitList.forEach(v => {
        const day = new Date(v.in).toLocaleDateString("ko-KR", { weekday: "short" });
        if (!dayMap[day]) dayMap[day] = [];
        dayMap[day].push(v.durationMinutes);
      });
      const weekly = Object.entries(dayMap).map(([name, values]) => ({
        name,
        time: Math.round(values.reduce((a, b) => a + b, 0) / values.length),
      }));
      setWeeklyData(weekly);
    } catch (err) {
      console.error("초기 API 오류", err);
    }
  };

  const handleConfirm = async () => {
    setIsConfirmed(true);
    const token = localStorage.getItem("token");
    const cctvId = getCctvId(selectedCCTV);
    const params = new URLSearchParams({
      startDate,
      endDate,
      startTime,
      endTime,
      cctvId,
    });

    try {
      const res = await fetch(`http://localhost:8080/api/visits?${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      const visitList = data.visits || [];

      if (visitList.length === 0) {
        setNoData(true);
        setDailyChartData([]);
        return;
      }

      setNoData(false);

      const dateRange = getDateRange(startDate, endDate);
      const durationMap = {}, countMap = {};
      visitList.forEach(v => {
        const dateKey = new Date(v.in).toISOString().split("T")[0];
        if (!durationMap[dateKey]) {
          durationMap[dateKey] = 0;
          countMap[dateKey] = 0;
        }
        durationMap[dateKey] += v.durationMinutes;
        countMap[dateKey] += 1;
      });
      const dailyData = dateRange.map(date => ({
        name: date.slice(5),
        value: countMap[date] ? Math.round(durationMap[date] / countMap[date]) : 0
      }));
      setDailyChartData(dailyData);
    } catch (err) {
      console.error("조건 검색 API 오류", err);
    }
  };

  const formatTimeLabel = (minutes) => {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    if (h === 0) return `${m}분`;
    if (m === 0) return `${h}시간`;
    return `${h}시간 ${m}분`;
  };

  useEffect(() => {
    if (!isConfirmed) fetchInitialStayData();
  }, [selectedCCTV]);

  const commonTooltip = <Tooltip formatter={(value) => [`${value}분`, "평균 체류시간"]} />;
  const commonXAxis = (
    <XAxis dataKey="name" interval="preserveStartEnd" angle={-45} dy={10} textAnchor="end" />
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
                <div className="today-sales" style={{ display: "flex", flexDirection: "column", alignItems: "center", paddingBottom: "40px", backgroundColor: "#fff" }}>

                  {/* 날짜 선택 필터 */}
                  <div style={{ width: "80%", display: "flex", justifyContent: "space-between", marginBottom: "30px" }}>
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
                    <button onClick={handleConfirm} style={{ padding: "10px 20px", borderRadius: "5px", backgroundColor: "#5D5FEF", color: "white", border: "none" }}>확인</button>
                  </div>

                  {/* 조건부 렌더링 */}
                  {isConfirmed ? (
                    noData ? (
                      <div style={{ fontSize: "18px", color: "#888", padding: "60px 0" }}>📭 조건에 맞는 방문자 데이터가 없습니다.</div>
                    ) : (
                      <div style={{ width: "90%", backgroundColor: "#fff", padding: "30px", borderRadius: "20px", boxShadow: "0 0 10px rgba(0,0,0,0.1)", display: "flex", flexDirection: "column", alignItems: "center" }}>
                        <div style={{ fontSize: "20px", fontWeight: "600", marginBottom: "20px" }}>기간 내 일간 평균 체류시간</div>
                        <ResponsiveContainer width="100%" height={350}>
                          <BarChart
                            data={dailyChartData}
                            margin={{ top: 40, right: 30, left: 20, bottom: 50 }}
                          >
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis
                              dataKey="name"
                              interval="preserveStartEnd "
                              angle={-45}
                              textAnchor="end"
                            />
                            <YAxis unit="분" />
                            <Tooltip formatter={(value) => [`${value}분`, "평균 체류시간"]} />                        
                            <Bar dataKey="value" fill="#4A5CFF" radius={[10, 10, 0, 0]} />
                            <ReferenceLine y={dailyAvg} stroke="#FF4D4F" strokeWidth={2} strokeDasharray="4 4" label={{ value: `평균 ${formatTimeLabel(dailyAvg)}`, position: "top", fill: "#FF4D4F", fontSize: 12 }} />
                          </BarChart>
                        </ResponsiveContainer>
                      </div>
                    )
                  ) : (
                    <div style={{ width: "90%", backgroundColor: "#fff", padding: "30px", borderRadius: "20px", boxShadow: "0 0 10px rgba(0,0,0,0.1)", display: "flex", flexDirection: "column", alignItems: "center" }}>
                      <div style={{ textAlign: "center", marginBottom: "40px", display: "flex", flexDirection: "column", alignItems: "center", position: "relative" }}>
                        <div style={{ fontSize: "20px", fontWeight: "600", marginBottom: "10px" }}>최근 한 달 평균 체류시간</div>
                        <RadialBarChart width={250} height={250} cx={125} cy={125} innerRadius={80} outerRadius={100} barSize={12} data={[{ name: "BG", value: 100, fill: "#EDEDED" }, { name: "Stay", value: percentage, fill: "#4A5CFF" }]} startAngle={90} endAngle={-270}>
                          <RadialBar clockWise dataKey="value" cornerRadius={10} />
                        </RadialBarChart>
                        <div style={{ position: "absolute", top: "50%", left: "50%", transform: "translate(-50%, -50%)", fontSize: "20px", fontWeight: "bold", color: "#444" }}>{formatTime(dailyAvg)}</div>
                      </div>
                      <div style={{ width: "100%" }}>
                        <div style={{ fontSize: "20px", fontWeight: "600", marginBottom: "20px" }}>요일별 평균 체류시간</div>
                        <ResponsiveContainer width="100%" height={300}>
                          <BarChart data={weeklyData}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="name" />
                            <YAxis unit="분" />
                            <Tooltip formatter={(value) => [`${value}분`, "평균 체류시간"]} />
                            <Bar dataKey="time" fill="#4A5CFF" radius={[10, 10, 0, 0]} />
                          </BarChart>
                        </ResponsiveContainer>
                      </div>
                    </div>
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

export default StatisticsPage3;