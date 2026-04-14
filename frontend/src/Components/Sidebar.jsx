import { openUrl } from '@tauri-apps/plugin-opener';
import { WebviewWindow } from "@tauri-apps/api/webviewWindow";

import "./Sidebar.css";

import jan from "../resources/assets/images/Months/January.gif?url";
import feb from "../resources/assets/images/Months/February.gif?url";
import mar from "../resources/assets/images/Months/March.gif?url";
import apr from "../resources/assets/images/Months/April.gif?url";
import may from "../resources/assets/images/Months/May.gif?url";
import jun from "../resources/assets/images/Months/June.gif?url";
import jul from "../resources/assets/images/Months/July.gif?url";
import aug from "../resources/assets/images/Months/August.gif?url";
import sep from "../resources/assets/images/Months/September.gif?url";
import oct from "../resources/assets/images/Months/October.gif?url";
import nov from "../resources/assets/images/Months/November.gif?url";
import dec from "../resources/assets/images/Months/December.gif?url";

import n0i from "../resources/assets/images/Numbers/0 Inverted.gif?url";
import n1i from "../resources/assets/images/Numbers/1 Inverted.gif?url";
import n2i from "../resources/assets/images/Numbers/2 Inverted.gif?url";
import n3i from "../resources/assets/images/Numbers/3 Inverted.gif?url";
import n4i from "../resources/assets/images/Numbers/4 Inverted.gif?url";
import n5i from "../resources/assets/images/Numbers/5 Inverted.gif?url";
import n6i from "../resources/assets/images/Numbers/6 Inverted.gif?url";
import n7i from "../resources/assets/images/Numbers/7 Inverted.gif?url";
import n8i from "../resources/assets/images/Numbers/8 Inverted.gif?url";
import n9i from "../resources/assets/images/Numbers/9 Inverted.gif?url";
import n10i from "../resources/assets/images/Numbers/10 Inverted.gif?url";
import n11i from "../resources/assets/images/Numbers/11 Inverted.gif?url";
import n12i from "../resources/assets/images/Numbers/12 Inverted.gif?url";
import n13i from "../resources/assets/images/Numbers/13 Inverted.gif?url";
import n14i from "../resources/assets/images/Numbers/14 Inverted.gif?url";
import n15i from "../resources/assets/images/Numbers/15 Inverted.gif?url";
import n16i from "../resources/assets/images/Numbers/16 Inverted.gif?url";
import n17i from "../resources/assets/images/Numbers/17 Inverted.gif?url";
import n18i from "../resources/assets/images/Numbers/18 Inverted.gif?url";
import n19i from "../resources/assets/images/Numbers/19 Inverted.gif?url";
import n20i from "../resources/assets/images/Numbers/20 Inverted.gif?url";
import n21i from "../resources/assets/images/Numbers/21 Inverted.gif?url";
import n22i from "../resources/assets/images/Numbers/22 Inverted.gif?url";
import n23i from "../resources/assets/images/Numbers/23 Inverted.gif?url";
import n24i from "../resources/assets/images/Numbers/24 Inverted.gif?url";
import n25i from "../resources/assets/images/Numbers/25 Inverted.gif?url";
import n26i from "../resources/assets/images/Numbers/26 Inverted.gif?url";
import n27i from "../resources/assets/images/Numbers/27 Inverted.gif?url";
import n28i from "../resources/assets/images/Numbers/28 Inverted.gif?url";
import n29i from "../resources/assets/images/Numbers/29 Inverted.gif?url";
import n30i from "../resources/assets/images/Numbers/30 Inverted.gif?url";
import n31i from "../resources/assets/images/Numbers/31 Inverted.gif?url";

import sLetter from "../resources/assets/images/Letters/S.gif?url";
import mLetter from "../resources/assets/images/Letters/M.gif?url";
import tLetter from "../resources/assets/images/Letters/T.gif?url";
import wLetter from "../resources/assets/images/Letters/W.gif?url";
import fLetter from "../resources/assets/images/Letters/F.gif?url";

import FAQ from "../resources/assets/images/ShapesSigns/FAQ.gif?url";
import Chat from "../resources/assets/images/ShapesSigns/Chat.gif?url";

const MONTHS = [jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec];

const YEAR_DIGITS = [n0i, n1i, n2i, n3i, n4i, n5i, n6i, n7i, n8i, n9i];

const DAY_GIFS = [
  null,
  n1i, n2i, n3i, n4i, n5i, n6i, n7i, n8i, n9i,
  n10i, n11i, n12i, n13i, n14i, n15i, n16i, n17i, n18i, n19i,
  n20i, n21i, n22i, n23i, n24i, n25i, n26i, n27i, n28i, n29i,
  n30i, n31i
];

const DAY_LABELS = [sLetter, mLetter, tLetter, wLetter, tLetter, fLetter, sLetter];

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
        {DAY_LABELS.map((gif, i) => (
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

      <a
        className="faq-link"
        title="About/FAQ"
        onClick={(e) => {
          e.preventDefault();
          openUrl("https://www.marqed.it/CustomCalendar");
        }}
        style={{ cursor: "pointer" }}
      >
        <img src={FAQ} className="faq-header" alt="FAQ" />
      </a>

      <a
        className="chat-link"
        title="Calisgh Bud/Chat Assistant"
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