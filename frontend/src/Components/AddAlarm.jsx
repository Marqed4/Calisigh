import { useState, useEffect } from "react";
import { invoke } from "@tauri-apps/api/core";
import { listen } from "@tauri-apps/api/event";
import WinterBackground from "../resources/assets/images/Winter Forest.gif";
import Sidebar from "./Sidebar.jsx";
import CalendarGrid from "./CalendarGrid.jsx";
import "./Home.css";

export default function Home() {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [alarms, setAlarms] = useState([]);

  async function loadAlarms() {
    try {
      const res = await fetch("http://localhost:4567/api/alarms");
      const data = await res.json();
      setAlarms(data);
    } catch (err) {
      console.error("Failed to load alarms:", err);
    }
  }

  useEffect(() => {
    loadAlarms();
    const unlisten = listen("alarm-saved", () => loadAlarms());
    return () => { unlisten.then(f => f()); };
  }, []);

  function changeMonth(offset) {
    const newDate = new Date(currentDate);
    newDate.setMonth(newDate.getMonth() + offset);
    setCurrentDate(newDate);
  }

  function getCalendarDays(date) {
    const year = date.getFullYear();
    const month = date.getMonth();
    const startDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const days = [];
    for (let i = 0; i < startDay; i++) days.push(null);
    for (let d = 1; d <= daysInMonth; d++) days.push(new Date(year, month, d));
    return days;
  }

  async function openAlarmWindow(date) {
    if (!date) return;
    try {
      await invoke("open_alarm_window", {
        day: date.getDate(),
        month: currentDate.getMonth() + 1,
        year: currentDate.getFullYear(),
      });
    } catch (err) {
      console.error("Failed to open alarm window:", err);
    }
  }

  const calendarDays = getCalendarDays(currentDate);

  return (
    <div
      className="background-wrapper"
      style={{ backgroundImage: `url(${WinterBackground})` }}
    >
      <div className="app-container">

        <Sidebar currentDate={currentDate} calendarDays={calendarDays} />

        <main className="main">
          <div className="top-nav">
            <button className="nav-btn" onClick={() => changeMonth(-1)}>◀</button>
            <h1 className="main-month">
              {currentDate.toLocaleString("default", { month: "long" })}{" "}
              {currentDate.getFullYear()}
            </h1>
            <button className="nav-btn" onClick={() => changeMonth(1)}>▶</button>
          </div>

          <CalendarGrid
            calendarDays={calendarDays}
            currentDate={currentDate}
            alarms={alarms}
            onDayClick={openAlarmWindow}
          />
        </main>

      </div>
    </div>
  );
}
