import { useState, useEffect } from "react";
import { getCurrentWindow } from "@tauri-apps/api/window";
import FallBackground from "../resources/assets/images/Backgrounds/Fall Forest.gif";
import WinterBackground from "../resources/assets/images/Backgrounds/Winter Forest.gif";
import SpringBackground from "../resources/assets/images/Backgrounds/Spring Forest.gif";
import SummerBackground from "../resources/assets/images/Backgrounds/Summer Forest.gif";
import "./AddAlarm.css";
import "./Settings.css";

const BACKGROUNDS = [
  { label: "Fall",   value: "fall",   src: FallBackground },
  { label: "Winter", value: "winter", src: WinterBackground },
  { label: "Spring", value: "spring", src: SpringBackground },
  { label: "Summer", value: "summer", src: SummerBackground },
];

export default function Settings() {
  const [selected, setSelected] = useState(
    localStorage.getItem("calisigh-bg") ?? "fall"
  );

  async function save() {
    localStorage.setItem("calisigh-bg", selected);
    await getCurrentWindow().close();
  }

  async function handleCancel() {
    await getCurrentWindow().close();
  }

  const current = BACKGROUNDS.find(b => b.value === selected);

  return (
    <>
      <div
        className="alarm-background"
        style={{ backgroundImage: `url(${current.src})` }}
      />
      <div className="alarm-window">
        <h2>Settings</h2>
        <p>Choose your background</p>

        <div className="settings-bg-grid">
          {BACKGROUNDS.map(bg => (
            <div
              key={bg.value}
              className={`settings-bg-option ${selected === bg.value ? "selected" : ""}`}
              onClick={() => setSelected(bg.value)}
            >
              <img src={bg.src} alt={bg.label} className="settings-bg-thumb" />
              <span>{bg.label}</span>
            </div>
          ))}
        </div>

        <div className="alarm-buttons">
          <button onClick={save}>Save</button>
          <button onClick={handleCancel}>Cancel</button>
        </div>
      </div>
    </>
  );
}