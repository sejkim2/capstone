import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, RadialBarChart, RadialBar, LabelList
} from "recharts";
import "../MainPage/style.css";
import { mockRevisitData } from "../../data/mockRevisitDataPage4.js";

const StatisticsPage4 = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isCCTV = location.pathname === "/cctv";

  const today = new Date();
  const weekAgo = new Date();
  weekAgo.setDate(today.getDate() - 7);

  const [startDate, setStartDate] = useState(weekAgo.toISOString().split("T")[0]);
  const [endDate, setEndDate] = useState(today.toISOString().split("T")[0]);
  const [startTime, setStartTime] = useState("00:00:00");
  const [endTime, setEndTime] = useState("23:59:59");
  const [selectedCCTV, setSelectedCCTV] = useState("CCTV1");

  const [revisitRate, setRevisitRate] = useState(0);
  const [intervalData, setIntervalData] = useState([]);
  const [avgInterval, setAvgInterval] = useState(0);
  const [formatInterval, setFormatInterval] = useState("");
  const [isCustomPeriod, setIsCustomPeriod] = useState(false);

  const getCctvId = (name) => {
    const mapping = { CCTV1: 1, CCTV2: 2, CCTV3: 3, CCTV4: 4 };
    return mapping[name] || 1;
  };

  const formatToTimeStr = (totalMinutes) => {
    const d = Math.floor(totalMinutes / (60 * 24));
    const h = Math.floor((totalMinutes % (60 * 24)) / 60);
    const m = Math.floor(totalMinutes % 60);
    return `${d}일 ${h}시간 ${m}분`;
  };

  const fetchRevisitStats = async () => {
    const token = localStorage.getItem("token");
    const cctvId = getCctvId(selectedCCTV);
    const params = new URLSearchParams({ startDate, endDate, startTime, endTime, cctvId });

    const from = new Date(`${startDate}T${startTime}`);
    const to = new Date(`${endDate}T${endTime}`);
    const dummyEndDate = new Date("2025-05-20T00:00:00");

    let combinedData = {};

    try {
      const res = await fetch(`/api/visits/revisit?${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const apiRes = await res.json();
      const apiData = apiRes && typeof apiRes.revisit_data === "object" ? apiRes.revisit_data : {};
      console.log("✅ [API 응답]", apiData);

      // ✅ 1. 더미 데이터 병합
      if (from < dummyEndDate) {
        const dummyData = mockRevisitData.revisit_data || mockRevisitData || {};
        for (const [vid, visits] of Object.entries(dummyData)) {
          const filtered = visits.filter(v => {
            const inDate = new Date(v.in);
            return inDate >= from && inDate <= to;
          });
          if (filtered.length > 0) {
            combinedData[vid] = (combinedData[vid] || []).concat(filtered);
          }
        }
        console.log("🟨 [더미 병합 후]", combinedData);
      }

      // ✅ 2. API 데이터 병합
      if (apiData && typeof apiData === "object") {
        for (const [vid, visits] of Object.entries(apiData)) {
          combinedData[vid] = (combinedData[vid] || []).concat(visits);
        }
      }
      console.log("🟩 [최종 병합된 combinedData]", combinedData);

      // ✅ 3. 재방문자 필터
      const filtered = Object.values(combinedData).filter(visits => {
        const validVisits = visits.filter(v => v.in && v.out);
        return validVisits.length > 1;
      });
      const totalVehicles = filtered.length;
      const rate = totalVehicles > 0
        ? Math.round((totalVehicles / Object.keys(combinedData).length) * 100)
        : 0;
      setRevisitRate(rate);

      // ✅ 4. 재방문 간격 계산
      const intervals = [], dayMap = {};
      filtered.forEach(visits => {
        const validVisits = visits.filter(v => v.in && v.out);
        const sorted = validVisits
          .map(v => new Date(v.in))
          .sort((a, b) => a - b);

        for (let i = 1; i < sorted.length; i++) {
          const diff = (sorted[i] - sorted[i - 1]) / (1000 * 60);
          intervals.push(diff);
          const day = sorted[i].toLocaleDateString("ko-KR", { weekday: "short" });
          if (!dayMap[day]) dayMap[day] = [];
          dayMap[day].push(diff);
        }
      });

      const avg = intervals.length
        ? Math.round(intervals.reduce((a, b) => a + b, 0) / intervals.length)
        : 0;
      setAvgInterval(avg);
      setFormatInterval(formatToTimeStr(avg));

      const weekdayOrder = ["월", "화", "수", "목", "금", "토", "일"];
      const weekDataUnsorted = Object.entries(dayMap).map(([day, list]) => {
        const avgMin = Math.round(list.reduce((a, b) => a + b, 0) / list.length);
        return { name: day, value: avgMin, label: formatToTimeStr(avgMin) };
      });

      const weekData = weekdayOrder.map(day =>
        weekDataUnsorted.find(d => d.name === day) || {
          name: day,
          value: 0,
          label: "0일 0시간 0분",
        }
      );

      setIntervalData(weekData);
    } catch (err) {
      console.error("재방문률 API 오류", err);
    }
  };

  useEffect(() => {
    fetchRevisitStats();
  }, []);

  const handleConfirm = () => {
    setIsCustomPeriod(true);
    fetchRevisitStats();
  };

  const chartTitleRate = isCustomPeriod ? "누적기간 방문자 평균 재방문률" : "일일 방문자 평균 재방문률";
  const chartTitleInterval = isCustomPeriod ? "누적기간 평균 재방문 기간" : "주간 평균 재방문 기간";

  const radialData = [
    { name: "bg", value: 100, fill: "#EDEDED" },
    { name: "revisit", value: isNaN(revisitRate) ? 0 : revisitRate, fill: "#4A5CFF" },
  ];

  return (
    <div className="main-screen">
      <div className="screen">
        <div className="overlap-wrapper">
          <div className="overlap">
            <div className="overlap-group">
              {/* 사이드 및 상단바 생략 없이 */}
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
                      <option value="CCTV1">주차장</option>
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

                  <div style={{ display: "flex", flexDirection: "column", alignItems: "center", marginBottom: "30px" }}>
                    <div style={{ fontSize: "20px", fontWeight: "600", marginBottom: "10px" }}>
                      {chartTitleRate}
                    </div>
                    <div style={{ position: "relative", width: 250, height: 250 }}>
                      <RadialBarChart width={250} height={250} cx={125} cy={125} innerRadius={80} outerRadius={100} barSize={12} data={radialData} startAngle={90} endAngle={-270}>
                        <RadialBar dataKey="value" clockWise cornerRadius={10} />
                      </RadialBarChart>
                      <div style={{
                        position: "absolute", top: "50%", left: "50%",
                        transform: "translate(-50%, -50%)",
                        fontSize: "20px", fontWeight: "bold", color: "#444"
                      }}>
                        {revisitRate}%
                      </div>
                    </div>
                  </div>

                  <div style={{ textAlign: "center", marginBottom: 30 }}>
                    <div style={{ fontSize: 20 }}>{chartTitleInterval}</div>
                    <div style={{ fontSize: 24, fontWeight: "bold", color: "#333" }}>{formatInterval}</div>
                  </div>

                  <div style={{ width: "90%" }}>
                    <div style={{ fontSize: "18px", marginBottom: "10px" }}>요일별 평균 재방문 간격</div>
                    <ResponsiveContainer width="100%" height={350}>
                      <BarChart data={intervalData}
                        margin={{ top: 40, bottom: 20, left: 10, right: 10 }}
                      >
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="name" />
                        <YAxis hide />
                        <Tooltip formatter={(v) => [formatToTimeStr(v), "간격"]} />
                        <Bar dataKey="value" fill="#4A5CFF" radius={[10, 10, 0, 0]} barSize={90}>
                          <LabelList dataKey="label" position="top" style={{ fill: "#333", fontSize: 12, whiteSpace: "nowrap" }} />
                        </Bar>
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
  );
};

export default StatisticsPage4;
