export const defaultWeekdayChartData = {
    labels: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
    datasets: [{
      label: "요일별 방문자 수",
      data: [10, 20, 30, 40, 60, 80, 70], // 예시 값
      backgroundColor: "#5D5FEF",
    }],
  };
  
  export const defaultHourlyChartData = {
    labels: Array.from({ length: 24 }, (_, i) => `${i}시`),
    datasets: [{
      label: "시간대별 방문자 수",
      data: [
        5, 8, 10, 15, 25, 40, 50, 60, 80, 90, 100, 110,
        120, 110, 100, 90, 80, 70, 60, 50, 30, 20, 10, 5
      ],
      backgroundColor: "#5D5FEF",
    }],
  };
  
  export const defaultInOutData = {
    labels: [
      "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
      "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
      "21", "22", "23", "24"
    ],
    datasets: [
      {
        label: "유입자 수",
        data: [5, 10, 20, 30, 50, 80, 90, 100, 110, 120, 100, 90, 80, 70, 60, 50, 40, 30, 20, 15, 10, 5, 3, 2],
        backgroundColor: "rgba(54, 162, 235, 0.8)",
      },
      {
        label: "유출자 수",
        data: [-2, -3, -5, -10, -20, -30, -40, -50, -60, -70, -80, -90, -100, -90, -80, -70, -60, -50, -40, -30, -20, -10, -5, -3],
        backgroundColor: "rgba(255, 99, 132, 0.8)",
      },
    ],
  };