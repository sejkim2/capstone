import React from 'react';
import { useNavigate } from 'react-router-dom';

const SideMenu = () => {
  const navigate = useNavigate();
  
  return (
    <div className="side-menu">
      <div className="frame" onClick={() => navigate('/visitor-summary')}>
        <div className="icon">📊</div>
        <div className="text">방문자 통계</div>
      </div>
      <div className="frame" onClick={() => navigate('/cctv')}>
        <div className="icon">📷</div>
        <div className="text">CCTV</div>
      </div>
      <div className="frame" onClick={() => navigate('/')}>
        <div className="icon">🚪</div>
        <div className="text">Sign Out</div>
      </div>
    </div>
  );
};

export default SideMenu;