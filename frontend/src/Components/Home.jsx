import { useState, useEffect } from "react";
import { invoke } from "@tauri-apps/api/core";
import { listen } from "@tauri-apps/api/event";
import WinterBackground from "../resources/assets/images/Winter Forest.gif";
import FallBackground from "../resources/assets/images/Fall Forest.gif";
import "../Home.css";

import sun from "../resources/assets/images/Sun.gif";
import mon from "../resources/assets/images/Mon.gif";
import tue from "../resources/assets/images/Tue.gif";
import wed from "../resources/assets/images/Wed.gif";
import thu from "../resources/assets/images/Thu.gif";
import fri from "../resources/assets/images/Fri.gif";
import sat from "../resources/assets/images/Sat.gif";

const DAY_GIFS = [sun, mon, tue, wed, thu, fri, sat];

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

    const unlisten = listen("alarm-saved", () => {
      loadAlarms();
    });

    return () => {
      unlisten.then(f => f());
    };
  }, []);

  function changeMonth(offset) {
    const newDate = new Date(currentDate);
    newDate.setMonth(newDate.getMonth() + offset);
    setCurrentDate(newDate);
  }

  function getCalendarDays(date) {
    const year = date.getFullYear();
    const month = date.getMonth();

    const firstDay = new Date(year, month, 1);
    const startDay = firstDay.getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const days = [];

    for (let i = 0; i < startDay; i++) days.push(null);
    for (let d = 1; d <= daysInMonth; d++) {
      days.push(new Date(year, month, d));
    }

    return days;
  }

  async function openAlarmWindow(date) {
    if (!date) return;

    const day = date.getDate();
    const month = currentDate.getMonth() + 1;
    const year = currentDate.getFullYear();

    try {
      await invoke("open_alarm_window", { day, month, year });
    } catch (err) {
      console.error("Failed to open alarm window:", err);
    }
  }

  return (
    <div
      className="background-wrapper"
      style={{ backgroundImage: `url(${WinterBackground})` }}
    >
      <div className="app-container">

        {/* LEFT SIDEBAR */}
        <aside className="sidebar">
          <h2 className="sidebar-month">
            {currentDate.toLocaleString("default", { month: "long" })}{" "}
            {currentDate.getFullYear()}
          </h2>

          {/* Mini Calendar */}
          <div className="mini-grid">
            {["S","M","T","W","T","F","S"].map((d, i) => (
              <div key={i} className="mini-day-label">{d}</div>
            ))}

            {getCalendarDays(currentDate).map((date, i) => (
              <div key={i} className="mini-day">
                {date?.getDate()}
              </div>
            ))}
          </div>

          <div className="sidebar-item">Add calendar</div>
          <div className="sidebar-item">Gmail</div>
        </aside>

        {/* MAIN CALENDAR AREA */}
        <main className="main">

          {/* Top Navigation */}
          <div className="top-nav">
            <button className="nav-btn" onClick={() => changeMonth(-1)}>◀</button>
            <h1 className="main-month">
              {currentDate.toLocaleString("default", { month: "long" })}{" "}
              {currentDate.getFullYear()}
            </h1>
            <button className="nav-btn" onClick={() => changeMonth(1)}>▶</button>
          </div>

          {/* Full Calendar Grid */}
          <div className="calendar-grid">
            {DAY_GIFS.map((gif, i) => (
              <div key={i} className="day-label">
                <img src={gif} alt={["Sun","Mon","Tue","Wed","Thu","Fri","Sat"][i]} className="day-label-gif" />
              </div>
            ))}

            {getCalendarDays(currentDate).map((date, i) => (
              <div
                key={i}
                className={`day-cell ${
                  date &&
                  date.toDateString() === new Date().toDateString()
                    ? "today"
                    : ""
                }`}
                onClick={() => openAlarmWindow(date)}
              >
                {date && (
                  <>
                    <div className="day-number">{date.getDate()}</div>

                    {alarms.some(a =>
                      a.time?.startsWith(date.toISOString().split("T")[0])
                    ) && <div className="alarm-dot" />}
                  </>
                )}
              </div>
            ))}
          </div>

        </main>
      </div>
    </div>
  );
}