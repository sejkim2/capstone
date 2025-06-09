import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css";

const formatDate = (date) => date.toISOString().split("T")[0];
const today = new Date();
const yesterday = new Date();
yesterday.setDate(today.getDate() - 1);

const CCTVPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isCCTV = location.pathname === "/cctv";
  const isMain = location.pathname === "/main";

  const [activeTab, setActiveTab] = useState("cctv");
  const [startDate, setStartDate] = useState(formatDate(yesterday));
  const [endDate, setEndDate] = useState(formatDate(today));
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("18:00");
  const [selectedCCTV, setSelectedCCTV] = useState("CCTV1");

  const [videoList, setVideoList] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const CCTVStream = ({ cctvId }) => {
    const canvasRef = React.useRef(null);
    const socketRef = React.useRef(null);

    useEffect(() => {
      const canvas = canvasRef.current;
      if (!canvas) return;

      const ctx = canvas.getContext("2d");
      if (!ctx) return;

      const socket = new WebSocket(`/ws/stream-view?cctvId=${cctvId}`);

      socketRef.current = socket;
      socket.binaryType = "arraybuffer";
      const img = new Image();

      socket.onmessage = (event) => {
        const blob = new Blob([event.data], { type: "image/jpeg" });
        const url = URL.createObjectURL(blob);
        img.onload = () => {
          ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
          URL.revokeObjectURL(url);
        };
        img.src = url;
      };

      socket.onerror = (e) => {
        console.error(`🛑 WebSocket Error (CCTV${cctvId}):`, e);
      };

      socket.onclose = () => {
        console.log(`🔌 WebSocket Closed (CCTV${cctvId})`);
      };

      return () => {
        socket.close();
      };
    }, [cctvId]);

    const cctvNameMap = {
      1: "주차장",
      2: "2층입구",
      3: "매장입구",
    };
    
    return (
      <div style={{ textAlign: "center" }}>
        <div style={{ fontWeight: "600", marginBottom: "8px" }}>
          {cctvNameMap[cctvId] || `CCTV${cctvId}`}
        </div>
        <canvas
          ref={canvasRef}
          width={480}
          height={270}
          style={{
            border: "1px solid #ddd",
            borderRadius: "8px",
            backgroundColor: "#000",
          }}
        />
      </div>
    );
  };

  const fetchVideos = async () => {
    try {
      const cctvId = selectedCCTV.replace("CCTV", "");
      const token = localStorage.getItem("token"); // 🔑 토큰 가져오기

      const response = await fetch(
        `/api/videos?cctvId=${cctvId}&startDate=${startDate}&startTime=${startTime}&endDate=${endDate}&endTime=${endTime}&page=${currentPage}&size=5`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`, // ✅ 토큰 추가
          },
        }
      );

      if (!response.ok) throw new Error("영상 데이터를 불러오지 못했습니다.");

      const data = await response.json();
      setVideoList(data.content || []);
      setTotalPages(data.totalPages || 0);
      console.log(`✅ 실데이터 ${data.content?.length || 0}건, 총 페이지 ${data.totalPages}`);
    } catch (error) {
      console.error("📛 fetchVideos error:", error);
      setVideoList([]);
      setTotalPages(0);
    }
  };

  useEffect(() => {
    if (activeTab === "record") {
      fetchVideos();
    }
  }, [currentPage, activeTab]);

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
                      className={`frame-2 ${isMain ? "active" : ""}`}
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
                        alt="Webcam"
                        src="https://c.animaapp.com/DAdHcKHy/img/webcam-video--work-video-meeting-camera-company-conference-offic@2x.png"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div className="top-bar">
                <div className="div-wrapper">
                  <div className="text-wrapper-4">CCTV</div>
                </div>
              </div>
            </div>

            <div className="todays-sales">
              <div
                style={{
                  display: "flex",
                  justifyContent: "center",
                  gap: "20px",
                  marginTop: "30px",
                  marginBottom: "20px",
                }}
              >
                <button
                  onClick={() => setActiveTab("cctv")}
                  style={{
                    backgroundColor: activeTab === "cctv" ? "#0056b3" : "#ccc",
                    color: "#fff",
                    padding: "10px 20px",
                    border: "none",
                    borderRadius: "5px",
                    cursor: "pointer",
                  }}
                >
                  CCTV
                </button>
                <button
                  onClick={() => setActiveTab("record")}
                  style={{
                    backgroundColor: activeTab === "record" ? "#0056b3" : "#ccc",
                    color: "#fff",
                    padding: "10px 20px",
                    border: "none",
                    borderRadius: "5px",
                    cursor: "pointer",
                  }}
                >
                  녹화 스트리밍
                </button>
              </div>

              {activeTab === "cctv" ? (
                <div
                  className="today-sales"
                  style={{
                    height: "750px",
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr",
                    gap: "20px",
                    padding: "30px",
                    backgroundColor: "#ffffff",
                    overflowY: "auto",
                  }}
                >
                  <CCTVStream key={1} cctvId={1} />
                  <CCTVStream key={2} cctvId={2} />
                  <CCTVStream key={3} cctvId={3} />
                </div>
              ) : (
                <div
                  className="today-sales"
                  style={{
                    height: "750px",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "flex-start",
                    alignItems: "center",
                    fontSize: "18px",
                    fontWeight: "400",
                    color: "#333",
                    backgroundColor: "#ffffff",
                    paddingTop: "30px",
                    gap: "20px",
                  }}
                >
                  <div
                    className="date-time-picker"
                    style={{
                      width: "80%",
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
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
                      <option value="CCTV1">주차장</option>
                      <option value="CCTV2">2층입구</option>
                      <option value="CCTV3">매장입구</option>
                    </select>

                    <div style={{ display: "flex", justifyContent: "space-between", width: "70%" }}>
                      <div>
                        <input
                          type="date"
                          value={startDate}
                          onChange={(e) => setStartDate(e.target.value)}
                          style={{ padding: "10px", width: "50%", height: "40px" }}
                        />
                        <input
                          type="time"
                          value={startTime}
                          onChange={(e) => setStartTime(e.target.value)}
                          style={{ padding: "10px", width: "45%", height: "40px" }}
                        />
                      </div>
                      <span style={{ margin: "0 10px", fontSize: "20px", alignSelf: "center" }}>~</span>
                      <div>
                        <input
                          type="date"
                          value={endDate}
                          onChange={(e) => setEndDate(e.target.value)}
                          style={{ padding: "10px", width: "50%", height: "40px" }}
                        />
                        <input
                          type="time"
                          value={endTime}
                          onChange={(e) => setEndTime(e.target.value)}
                          style={{ padding: "10px", width: "45%", height: "40px" }}
                        />
                      </div>
                    </div>

                    <button
                      onClick={fetchVideos}
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

                  <div
                    style={{
                      marginTop: "40px",
                      width: "80%",
                      minHeight: "500px",
                      maxHeight: "500px", // ✅ 추가: 스크롤 가능하도록 최대 높이 제한
                      backgroundColor: "#fafafa",
                      borderRadius: "8px",
                      padding: "20px",
                      display: "flex",
                      flexDirection: "column",
                      gap: "20px",
                      overflowY: "auto", // ✅ 세로 스크롤 가능
                    }}
                  >
                    {videoList.length > 0 ? (
                      <>
                        {videoList.map((video, index) => (
                          <div
                            key={index}
                            style={{
                              display: "flex",
                              alignItems: "center",
                              gap: "20px",
                              padding: "10px",
                              border: "1px solid #ddd",
                              borderRadius: "6px",
                            }}
                          >
                            <img
                              src={video.thumbnailUrl}
                              alt={`Thumbnail ${index}`}
                              style={{ width: "180px", height: "100px", objectFit: "cover" }}
                            />
                            <div style={{ flex: 1 }}>
                              <div>🕒 {new Date(video.timestamp).toLocaleString()}</div>
                              <a
                                href={video.videoUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                style={{ color: "#007bff" }}
                              >
                                스트리밍 보기
                              </a>
                            </div>
                          </div>
                        ))}

                        {/* ✅ 페이지네이션 버튼 */}
                        <div style={{ display: "flex", justifyContent: "center", marginTop: "20px", gap: "10px" }}>
                          {Array.from({ length: totalPages }, (_, i) => (
                            <button
                              key={i}
                              onClick={() => {
                                setCurrentPage(i);
                                window.scrollTo({ top: 0, behavior: "smooth" });
                              }}
                              style={{
                                padding: "8px 12px",
                                borderRadius: "4px",
                                backgroundColor: currentPage === i ? "#5D5FEF" : "#eee",
                                color: currentPage === i ? "#fff" : "#333",
                                border: "1px solid #ccc",
                                cursor: "pointer",
                              }}
                            >
                              {i + 1}
                            </button>
                          ))}
                        </div>
                      </>
                    ) : (
                      <div style={{ color: "#999", fontSize: "18px" }}>🎥 영상이 없습니다.</div>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CCTVPage;
