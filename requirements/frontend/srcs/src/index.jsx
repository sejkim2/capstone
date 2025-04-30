import React from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import LoginPage from "./screens/LoginPage";
import { Screen as MainPage } from "./screens/MainPage";
import CCTVPage from "./screens/CCTVPage";
import VisitorSummaryPage from "./screens/StatisticsPage1";  // VisitorSummaryPage 추가
import StatisticsPage2 from "./screens/StatisticsPage2";  // 두 번째 페이지
import StatisticsPage3 from "./screens/StatisticsPage3";  // 세 번째 페이지
import StatisticsPage4 from "./screens/StatisticsPage4";  // 네 번째 페이지

createRoot(document.getElementById("app")).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/cctv" element={<CCTVPage />} />
        <Route path="/visitor-summary" element={<VisitorSummaryPage />} />
        <Route path="/statistics-page-2" element={<StatisticsPage2 />} />
        <Route path="/statistics-page-3" element={<StatisticsPage3 />} />
        <Route path="/statistics-page-4" element={<StatisticsPage4 />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
