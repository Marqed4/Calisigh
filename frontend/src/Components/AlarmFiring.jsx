import { useEffect, useState } from "react";
import { getCurrentWindow } from "@tauri-apps/api/window";
import Bell from "../resources/assets/images/Signs/Bell.gif?url";
import { DefaultBackgrounds } from "../resources/assets/images/Backgrounds/index.js";
import "./AlarmFiring.css";

// Same BG_MAP as the other windows, keys match localStorage
const BG_MAP = {
  barn: DefaultBackgrounds.Barn,
  lakeside: DefaultBackgrounds.Lake,
  peace: DefaultBackgrounds.Peace,
  silos: DefaultBackgrounds.Silo,
  summer: DefaultBackgrounds.Summer,
  tree: DefaultBackgrounds.Tree,
  fall: DefaultBackgrounds.Fall,
  winter: DefaultBackgrounds.Winter,
};

export default function AlarmFiring() {
  // Pull the alarm title and description from the query params
  const params = new URLSearchParams(window.location.search);
  const title = params.get("title") || "Alarm";
  const desc  = params.get("desc")  || "";

  const [stopping, setStopping] = useState(false);
  const bg = BG_MAP[localStorage.getItem("calisigh-bg") ?? "fall"];

  // Stop the sound and clear the firing state, then close
  async function stopAlarm() {
    setStopping(true);
    try {
      await fetch("http://localhost:4567/api/sounds/stop", { method: "POST" });
      await fetch("http://localhost:4567/api/alarms/firing", { method: "DELETE" });
    } catch (e) {
      console.error("Failed to stop alarm:", e);
    }
    await getCurrentWindow().close();
  }

  /*
    If the user closes the window with the X button instead of Stop,
    we still need to stop the sound and clear the firing state.
    preventDefault lets us do cleanup before actually destroying the window.
  */
  useEffect(() => {
    const win = getCurrentWindow();
    let unlisten;
    win.onCloseRequested(async (event) => {
      event.preventDefault();
      try {
        await fetch("http://localhost:4567/api/sounds/stop", { method: "POST" });
        await fetch("http://localhost:4567/api/alarms/firing", { method: "DELETE" });
      } catch {}
      if (unlisten) (await unlisten)();
      await win.destroy();
    });
    return () => { if (unlisten) unlisten.then(f => f()); };
  }, []);

  return (
    <>
      <div className="alarm-background" style={{ backgroundImage: `url(${bg})` }} />
      <div className="alarm-firing-window">
        <img src={Bell} className="alarm-firing-icon" alt="Alarm" />
        <h2 className="alarm-firing-title">{title}</h2>
        {desc && <p className="alarm-firing-desc">{desc}</p>}
        <button
          className={`alarm-firing-stop ${stopping ? "alarm-firing-stop--stopping" : ""}`}
          onClick={stopAlarm}
          disabled={stopping}
        >
          {stopping ? "Stopping..." : "Stop Alarm"}
        </button>
      </div>
    </>
  );
}