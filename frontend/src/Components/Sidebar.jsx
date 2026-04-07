import { openUrl } from '@tauri-apps/plugin-opener';
import "./Sidebar.css";

import jan from "../resources/assets/images/Months/January.gif";
import feb from "../resources/assets/images/Months/February.gif";
import mar from "../resources/assets/images/Months/March.gif";
import apr from "../resources/assets/images/Months/April.gif";
import may from "../resources/assets/images/Months/May.gif";
import jun from "../resources/assets/images/Months/June.gif";
import jul from "../resources/assets/images/Months/July.gif";
import aug from "../resources/assets/images/Months/August.gif";
import sep from "../resources/assets/images/Months/September.gif";
import oct from "../resources/assets/images/Months/October.gif";
import nov from "../resources/assets/images/Months/November.gif";
import dec from "../resources/assets/images/Months/December.gif";

import n0i from "../resources/assets/images/Numbers/0 Inverted.gif";
import n1i from "../resources/assets/images/Numbers/1 Inverted.gif";
import n2i from "../resources/assets/images/Numbers/2 Inverted.gif";
import n3i from "../resources/assets/images/Numbers/3 Inverted.gif";
import n4i from "../resources/assets/images/Numbers/4 Inverted.gif";
import n5i from "../resources/assets/images/Numbers/5 Inverted.gif";
import n6i from "../resources/assets/images/Numbers/6 Inverted.gif";
import n7i from "../resources/assets/images/Numbers/7 Inverted.gif";
import n8i from "../resources/assets/images/Numbers/8 Inverted.gif";
import n9i from "../resources/assets/images/Numbers/9 Inverted.gif";
import n10i from "../resources/assets/images/Numbers/10 Inverted.gif";
import n11i from "../resources/assets/images/Numbers/11 Inverted.gif";
import n12i from "../resources/assets/images/Numbers/12 Inverted.gif";
import n13i from "../resources/assets/images/Numbers/13 Inverted.gif";
import n14i from "../resources/assets/images/Numbers/14 Inverted.gif";
import n15i from "../resources/assets/images/Numbers/15 Inverted.gif";
import n16i from "../resources/assets/images/Numbers/16 Inverted.gif";
import n17i from "../resources/assets/images/Numbers/17 Inverted.gif";
import n18i from "../resources/assets/images/Numbers/18 Inverted.gif";
import n19i from "../resources/assets/images/Numbers/19 Inverted.gif";
import n20i from "../resources/assets/images/Numbers/20 Inverted.gif";
import n21i from "../resources/assets/images/Numbers/21 Inverted.gif";
import n22i from "../resources/assets/images/Numbers/22 Inverted.gif";
import n23i from "../resources/assets/images/Numbers/23 Inverted.gif";
import n24i from "../resources/assets/images/Numbers/24 Inverted.gif";
import n25i from "../resources/assets/images/Numbers/25 Inverted.gif";
import n26i from "../resources/assets/images/Numbers/26 Inverted.gif";
import n27i from "../resources/assets/images/Numbers/27 Inverted.gif";
import n28i from "../resources/assets/images/Numbers/28 Inverted.gif";
import n29i from "../resources/assets/images/Numbers/29 Inverted.gif";
import n30i from "../resources/assets/images/Numbers/30 Inverted.gif";
import n31i from "../resources/assets/images/Numbers/31 Inverted.gif";

import FAQ from "../resources/assets/images/ShapesSigns/FAQ.gif";

const MONTHS = [jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec];

const YEAR_DIGITS = [
  n0i, n1i, n2i, n3i, n4i, n5i, n6i, n7i, n8i, n9i
];

const DAY_GIFS = [
  null,
  n1i, n2i, n3i, n4i, n5i, n6i, n7i, n8i, n9i,
  n10i, n11i, n12i, n13i, n14i, n15i, n16i, n17i, n18i, n19i,
  n20i, n21i, n22i, n23i, n24i, n25i, n26i, n27i, n28i, n29i,
  n30i, n31i
];

export default function Sidebar({ currentDate, calendarDays }) {
  const monthGif = MONTHS[currentDate.getMonth()];

  return (
    <div className="sidebar">
      <div className="sidebar-month">
        {monthGif ? (
          <img
            src={monthGif}
            alt={currentDate.toLocaleString("default", { month: "long" })}
            className="sidebar-month-gif"
          />
        ) : (
          <span className="sidebar-month-text">
            {currentDate.toLocaleString("default", { month: "long" })}
          </span>
        )}

        <div className="sidebar-year-gifs">
          {String(currentDate.getFullYear())
            .split("")
            .map((digit, i) => (
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
        {["S", "M", "T", "W", "T", "F", "S"].map((d, i) => (
          <div key={i} className="mini-day-label">
            {d}
          </div>
        ))}

        {calendarDays.map((date, i) => (
          <div key={i} className="mini-day">
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
        ))}
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
        <img src={FAQ} className="details-header" alt="FAQ" />
      </a>
    </div>
  );
}
