// src/screens/CCTVPage/index.jsx
import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "../MainPage/style.css"; // 스타일 공유

const CCTVPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isCCTV = location.pathname === "/cctv";
  const isMain = location.pathname === "/main";

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
                    {/* 방문자 통계 */}
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

                    {/* CCTV */}
                    <div
                      className={`frame-3 ${isCCTV ? "active" : ""}`}
                      onClick={() => navigate("/cctv")}
                      style={{ cursor: "pointer" }}
                    >
                      <IconOutlineShoppingCart1 className="icon-outline" />
                      <div className="text-wrapper">CCTV</div>
                    </div>

                    {/* Sign Out */}
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

              {/* 상단 바 */}
              <div className="top-bar">
                <div className="div-wrapper">
                  <div className="text-wrapper-4">CCTV</div>
                </div>
              </div>
            </div>

            {/* 콘텐츠 영역 (통계 카드 대신 깔끔한 중앙 정렬 문구) */}
            <div className="todays-sales">
              <div
                className="today-sales"
                style={{
                  height: "850px",
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  fontSize: "24px",
                  fontWeight: "500",
                  color: "#444",
                  backgroundColor: "#ffffff",
                }}
              >
                <p style={{ margin: 0 }}>CCTV 콘텐츠가 표시될 영역입니다.</p>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  );
};

export default CCTVPage;
