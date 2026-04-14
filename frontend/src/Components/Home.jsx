import { useState, useEffect, useRef } from "react";
import { listen } from "@tauri-apps/api/event";
import { WebviewWindow } from "@tauri-apps/api/webviewWindow";
import SettingsGif from "../resources/assets/images/ShapesSigns/Settings.gif?url";
import WinterBackground from "../resources/assets/images/Backgrounds/Winter Forest.gif";
import FallBackground from "../resources/assets/images/Backgrounds/Fall Forest.gif";
import SpringBackground from "../resources/assets/images/Backgrounds/Spring Forest.gif";
import SummerBackground from "../resources/assets/images/Backgrounds/Summer Forest.gif";
import MonthYearDisplay from "./MonthYearDisplay.jsx";
import CalendarGrid from "./CalendarGrid.jsx";
import Sidebar from "./Sidebar.jsx";
import "./MonthYearDisplay.css";
import "./Home.css";

const BG_MAP = {
  fall: FallBackground,
  winter: WinterBackground,
  spring: SpringBackground,
  summer: SummerBackground,
};

async function openWindow(label, url, options = {}) {
  console.log("openWindow called", label);
  try {
    const existing = await WebviewWindow.getByLabel(label);
    if (existing) {
      await existing.show();
      await existing.setFocus();
      return;
    }
    const win = new WebviewWindow(label, {
      url,
      resizable: false,
      center: true,
      parent: "main",
      ...options,
    });
    win.once("tauri://error", (e) => console.error(`${label} error:`, e));
    win.once("tauri://created", () => console.log(`${label} created`));
  } catch (err) {
    console.error(`Failed to open ${label}:`, err);
  }
}

export default function Home() {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [alarms, setAlarms] = useState([]);
  const [gridSize, setGridSize] = useState(0);
  const mainRef = useRef(null);

const [bg, setBg] = useState(BG_MAP[localStorage.getItem("calisigh-bg") ?? "fall"]);

useEffect(() => {
  const onFocus = () => setBg(BG_MAP[localStorage.getItem("calisigh-bg") ?? "fall"]);
  window.addEventListener("focus", onFocus);
  return () => window.removeEventListener("focus", onFocus);
}, []);

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

  async function openAlarmWindow(date) {
    if (!date) return;
    await openWindow("add-alarm", `/add-alarm?day=${date.getDate()}&month=${currentDate.getMonth() + 1}&year=${currentDate.getFullYear()}`);
  }

  async function openEditWindow(alarm) {
    const dt = new Date(alarm.time);
    const query = new URLSearchParams({
      id: alarm.time,
      title: alarm.title,
      desc: alarm.desc ?? "",
      day: dt.getDate(),
      month: dt.getMonth() + 1,
      year: dt.getFullYear(),
      time: alarm.time.slice(11, 16),
    }).toString();
    await openWindow("view-edit-alarm", `/view-edit-alarm?${query}`);
  }

  const openSettingsWindow = () => openWindow("view-settings", "/view-settings", { title : "Settings"});

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

  const calendarDays = getCalendarDays(currentDate);

  return (
    <div className="background-wrapper" style={{ backgroundImage: `url(${bg})` }}>
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
        <a
          className="settings-link"
          title="Calisigh's Settings"
          onClick={(e) => { e.preventDefault(); openSettingsWindow(); }}
          style={{ cursor: "pointer" }}
        >
          <img src={SettingsGif} className="settings-header" alt="Settings" />
        </a>
      </div>
    </div>
  );
}