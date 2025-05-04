// src/screens/MainPage/index.jsx
import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { VectorStroke } from "../../components/VectorStroke";
import { IconOutlineShoppingCart1 } from "../../icons/IconOutlineShoppingCart1";
import "./style.css";

export const Screen = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const isMain = location.pathname === "/main";
  const isCCTV = location.pathname === "/cctv";

  const [userInfo, setUserInfo] = useState(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch("/api/users/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("인증 실패 또는 토큰 만료");
        }

        const data = await response.text();
        setUserInfo(data);
      } catch (error) {
        console.error("사용자 정보 로딩 실패:", error);
      }
    };

    fetchUserData();
  }, []);

  const handleSignOut = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  const handleVisitorSummaryClick = () => {
    navigate("/visitor-summary");
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
                      onClick={handleSignOut}
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
                  <div className="text-wrapper-4">Home</div>
                </div>
              </div>
            </div>

            <div className="todays-sales">
              <div className="today-sales">
                <div className="element" onClick={() => navigate("/statistics-page-3")} style={{ cursor: "pointer" }}>
                  <div className="overlap-2">
                    <div className="text-wrapper-5">Click to View Summary</div>
                    <div className="text-wrapper-6">고객 분포</div>
                    <div className="icon">
                      <div className="div-wrapper-2">
                        <div className="fastforward-clock">
                          <div className="overlap-group-2">
                            <img
                              className="vector-stroke-2"
                              alt="Vector stroke"
                              src="https://c.animaapp.com/DAdHcKHy/img/vector--stroke-.svg"
                            />
                            <img
                              className="ellipse-stroke"
                              alt="Ellipse stroke"
                              src="https://c.animaapp.com/DAdHcKHy/img/ellipse-1115--stroke-.svg"
                            />
                            <VectorStroke className="vector-2-stroke" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="overlap-group-wrapper" onClick={() => navigate("/statistics-page-2")} style={{ cursor: "pointer" }}>
                  <div className="overlap-3">
                    <div className="text-wrapper-5">Click to View Summary</div>
                    <div className="text-wrapper-7">방문객 통계</div>
                    <div className="disc-icon-wrapper">
                      <div className="disc-icon" />
                    </div>
                  </div>
                </div>

                <div className="element-2" onClick={() => navigate("/statistics-page-4")} style={{ cursor: "pointer" }}>
                  <div className="overlap-4">
                    <div className="login-arrow-enter-wrapper">
                      <div className="login-arrow-enter" />
                    </div>
                    <div className="text-wrapper-5">Click to View Summary</div>
                    <div className="text-wrapper-6">재방문률</div>
                  </div>
                </div>

                <div className="element-3" onClick={handleVisitorSummaryClick} style={{ cursor: "pointer" }}>
                  <div className="overlap-5">
                    <div className="text-wrapper-5">Click to View Summary</div>
                    <div className="text-wrapper-8">방문객 수 통계</div>
                    <div className="sales-icon-wrapper">
                      <div className="subtract-wrapper">
                        <div className="div-wrapper-2">
                          <img
                            className="subtract"
                            alt="Subtract"
                            src="https://c.animaapp.com/DAdHcKHy/img/subtract.svg"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="overlap-6">
                  <div className="group-2">
                    <div className="text-wrapper-9">오늘 방문자 수</div>
                    <div className="text-wrapper-10">visitor Summary</div>
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
