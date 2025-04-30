import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css";

const StatisticsPage4 = () => {
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

                    {/* Sign Out 버튼 */}
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
                  <div className="text-wrapper-4">Return rate</div>
                </div>
              </div>

              {/* 본문: 날짜, 시간, CCTV 선택 */}
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
                  {/* 날짜/시간/CCTV 선택 영역 */}
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

                  {/* 그래프는 없고, 선택 필터만 있음 */}
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
