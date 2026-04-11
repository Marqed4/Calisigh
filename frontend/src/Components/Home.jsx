import { useState, useEffect, useRef } from "react";
import { listen } from "@tauri-apps/api/event";
import { invoke } from "@tauri-apps/api/core";
import { WebviewWindow } from "@tauri-apps/api/webviewWindow";
import WinterBackground from "../resources/assets/images/Backgrounds/Winter Forest.gif";
import FallBackground from "../resources/assets/images/Backgrounds/Fall Forest.gif";
import MonthYearDisplay from "./MonthYearDisplay.jsx";
import CalendarGrid from "./CalendarGrid.jsx";
import Sidebar from "./Sidebar.jsx";
import "./MonthYearDisplay.css";
import "./Home.css";

export default function Home() {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [alarms, setAlarms] = useState([]);
  const [gridSize, setGridSize] = useState(0);
  const mainRef = useRef(null);

  function updateSize() {
    if (mainRef.current) {
      const { width, height } = mainRef.current.getBoundingClientRect();
      const navHeight = document.querySelector(".top-nav")?.getBoundingClientRect().height ?? 55;
      setGridSize(Math.min(width, height - navHeight));
    }
  }

  async function loadAlarms() {
    try {
      const res = await fetch("http://localhost:4567/api/alarms");
      const data = await res.json();
      setAlarms(data);
    } catch (err) {
      console.error("Failed to load alarms:", err);
    }
  }

  async function deleteAlarm(alarm) {
    try {
      await fetch("http://localhost:4567/api/alarms", {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ time: alarm.time }),
      });
      loadAlarms();
    } catch (err) {
      console.error("Failed to delete alarm:", err);
    }
  }

  async function openEditWindow(alarm) {
    const dt    = new Date(alarm.time);
    const day   = dt.getDate();
    const month = dt.getMonth() + 1;
    const year  = dt.getFullYear();
    const time  = alarm.time.slice(11, 16);

    const query = new URLSearchParams({
      id:    alarm.id,
      title: alarm.title,
      desc:  alarm.desc ?? "",
      day, month, year, time,
    }).toString();

    try {
      const existing = await WebviewWindow.getByLabel("view-edit-alarm");
      if (existing) {
        await existing.close();
      }

      const win = new WebviewWindow("view-edit-alarm", {
        url:         `/view-edit-alarm?${query}`,
        title:       "Edit Alarm",
        width:       320,
        height:      310,
        resizable:   false,
        alwaysOnTop: true,
      });

      win.once("tauri://error", (e) => {
        console.error("WebviewWindow error:", e);
      });
    } catch (err) {
      console.error("openEditWindow failed:", err);
    }
  }

  useEffect(() => {
    loadAlarms();
    const unlisten = listen("alarm-saved", () => loadAlarms());
    updateSize();
    window.addEventListener("resize", updateSize);
    return () => {
      unlisten.then(f => f());
      window.removeEventListener("resize", updateSize);
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
        day:   date.getDate(),
        month: currentDate.getMonth() + 1,
        year:  currentDate.getFullYear(),
      });
    } catch (err) {
      console.error("Failed to open alarm window:", err);
    }
  }

  const calendarDays = getCalendarDays(currentDate);

  return (
    <div
      className="background-wrapper"
      style={{ backgroundImage: `url(${FallBackground})` }}
    >
      <div className="app-container">
        <Sidebar currentDate={currentDate} calendarDays={calendarDays} />
        <main className="main" ref={mainRef}>
          <MonthYearDisplay
            currentDate={currentDate}
            onPrev={() => changeMonth(-1)}
            onNext={() => changeMonth(1)}
          />
          <CalendarGrid
            calendarDays={calendarDays}
            currentDate={currentDate}
            alarms={alarms}
            onDayClick={openAlarmWindow}
            onDeleteAlarm={deleteAlarm}
            onEditAlarm={openEditWindow}
            gridSize={gridSize}
          />
        </main>
      </div>
    </div>
  );
}