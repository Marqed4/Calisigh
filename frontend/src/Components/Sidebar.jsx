import { openUrl } from '@tauri-apps/plugin-opener';
import { WebviewWindow } from "@tauri-apps/api/webviewWindow";
import { useFontTheme } from "./FontPackage.jsx";
import "./Sidebar.css";

import FAQ  from "../resources/assets/images/Signs/FAQ.gif";
import Chat from "../resources/assets/images/Signs/Chat.gif";

/* Opens the chat assistant window, or focuses it if already open */
async function openChatWindow() {
  try {
    const existing = await WebviewWindow.getByLabel("view-chat-assistant");
    if (existing) {
      await existing.center();
      await existing.show();
      await existing.setFocus();
    } else {
      const win = new WebviewWindow("view-chat-assistant", {
        url: "/view-chat-assistant",
        title: "Chat",
        width: 420,
        height: 420,
        resizable: true,
        center: true,
      });
      win.once("tauri://error", (e) => console.error("chat error:", e));
    }
  } catch (err) {
    console.error("openChatWindow failed:", err);
  }
}

export default function Sidebar({ currentDate, calendarDays }) {

  /* Pull active font assets from context */
  const { fontTheme }   = useFontTheme();
  const GraffitiMonths  = fontTheme.Months;
  const GraffitiNumbers = fontTheme.Numbers;
  const GraffitiLetters = fontTheme.Letters;

  /* Built inside the function so they reflect the current font theme */
  const MONTHS = [
    GraffitiMonths.January,   GraffitiMonths.February, GraffitiMonths.March,
    GraffitiMonths.April,     GraffitiMonths.May,      GraffitiMonths.June,
    GraffitiMonths.July,      GraffitiMonths.August,   GraffitiMonths.September,
    GraffitiMonths.October,   GraffitiMonths.November, GraffitiMonths.December,
  ];

  const YEAR_DIGITS = [
    GraffitiNumbers.Date0, GraffitiNumbers.Date1, GraffitiNumbers.Date2,
    GraffitiNumbers.Date3, GraffitiNumbers.Date4, GraffitiNumbers.Date5,
    GraffitiNumbers.Date6, GraffitiNumbers.Date7, GraffitiNumbers.Date8,
    GraffitiNumbers.Date9,
  ];

  const DAY_GIFS = [
    null,
    GraffitiNumbers.Date1,  GraffitiNumbers.Date2,  GraffitiNumbers.Date3,
    GraffitiNumbers.Date4,  GraffitiNumbers.Date5,  GraffitiNumbers.Date6,
    GraffitiNumbers.Date7,  GraffitiNumbers.Date8,  GraffitiNumbers.Date9,
    GraffitiNumbers.Date10, GraffitiNumbers.Date11, GraffitiNumbers.Date12,
    GraffitiNumbers.Date13, GraffitiNumbers.Date14, GraffitiNumbers.Date15,
    GraffitiNumbers.Date16, GraffitiNumbers.Date17, GraffitiNumbers.Date18,
    GraffitiNumbers.Date19, GraffitiNumbers.Date20, GraffitiNumbers.Date21,
    GraffitiNumbers.Date22, GraffitiNumbers.Date23, GraffitiNumbers.Date24,
    GraffitiNumbers.Date25, GraffitiNumbers.Date26, GraffitiNumbers.Date27,
    GraffitiNumbers.Date28, GraffitiNumbers.Date29, GraffitiNumbers.Date30,
    GraffitiNumbers.Date31,
  ];

  /* Single-letter day headers for the mini calendar grid */
  const DAYS_INIT = [
    GraffitiLetters.S, GraffitiLetters.M, GraffitiLetters.T,
    GraffitiLetters.W, GraffitiLetters.T, GraffitiLetters.F,
    GraffitiLetters.S,
  ];

  const monthGif = MONTHS[currentDate.getMonth()];

  return (
    <div className="sidebar">
      <div className="sidebar-month">
        <img
          src={monthGif}
          alt={currentDate.toLocaleString("default", { month: "long" })}
          className="sidebar-month-gif"
        />
        <div className="sidebar-year-gifs">
          {String(currentDate.getFullYear()).split("").map((digit, i) => (
            <img
              key={i}
              src={YEAR_DIGITS[parseInt(digit)]}
              alt={digit}
              className="sidebar-number-gif"
            />
          ))}
        </div>
      </div>

      <div className="mini-grid">
        {DAYS_INIT.map((gif, i) => (
          <div key={i} className="mini-day-label">
            <img src={gif} alt={["S","M","T","W","T","F","S"][i]} className="mini-day-label-gif" />
          </div>
        ))}

        {calendarDays.map((date, i) => {
          const isToday = date && date.toDateString() === new Date().toDateString();
          return (
            <div key={i} className={`mini-day${isToday ? " mini-today" : ""}`}>
              <div className="mini-day-gif-wrapper">
                {date && (
                  <img
                    src={DAY_GIFS[date.getDate()]}
                    alt={date.getDate()}
                    className="mini-number-gif"
                  />
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Opens the FAQ/about page in the user's browser */}
      <a
        className="faq-link"
        title="About/FAQ"
        onClick={(e) => {
          e.preventDefault();
          openUrl("https://www.marqed.it/Calisigh");
        }}
        style={{ cursor: "pointer" }}
      >
        <img src={FAQ} className="faq-header" alt="FAQ" />
      </a>

      {/* Opens the Calisigh Bud chat assistant window */}
      <a
        className="chat-link"
        title="Calisigh Bud/Chat Assistant"
        onClick={(e) => {
          e.preventDefault();
          openChatWindow();
        }}
        style={{ cursor: "pointer" }}
      >
        <img src={Chat} className="chatbot-header" alt="Chat" />
      </a>
    </div>
  );
}