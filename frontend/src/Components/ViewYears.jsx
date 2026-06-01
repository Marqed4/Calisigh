import { useState } from "react";
import { useFontTheme } from "./FontPackage.jsx";
import "./ViewYears.css";

import leftarrow  from "../resources/assets/images/Signs/Reflective Left Arrow.gif";
import rightarrow from "../resources/assets/images/Signs/Reflective Right Arrow.gif";

/* Renders each digit of a year as a font-themed GIF */
function YearDigits({ year }) {

  /* Sub-component has its own hook call to access font context */
  const { fontTheme } = useFontTheme();
  const GraffitiNumbers = fontTheme.Numbers;

  const YEAR_DIGITS = [
    GraffitiNumbers.Date0, GraffitiNumbers.Date1, GraffitiNumbers.Date2,
    GraffitiNumbers.Date3, GraffitiNumbers.Date4, GraffitiNumbers.Date5,
    GraffitiNumbers.Date6, GraffitiNumbers.Date7, GraffitiNumbers.Date8,
    GraffitiNumbers.Date9,
  ];

  const Years = []

  return (
    <div className="view-years-cell-gifs">
      {String(year).split("").map((digit, i) => (
        <img
          key={i}
          src={YEAR_DIGITS[parseInt(digit)]}
          alt={digit}
          className="view-years-number-gif"
        />
      ))}
    </div>
  );
}

export default function ViewYears({ currentDate, onToggleYearView, onSelectYear }) {
  const [yearOffset, setYearOffset] = useState(0);
  const { fontTheme } = useFontTheme();

  const YEARS_PER_PAGE = 35;
  const START_YEAR     = new Date().getFullYear();

  /* Compute the visible year range based on current page offset */
  const start = START_YEAR + yearOffset * YEARS_PER_PAGE;
  const end   = start + YEARS_PER_PAGE - 1;

  const years = [];
  for (let y = start; y <= end; y++) {
    years.push(y);
  }

  const onPrev = () => setYearOffset(yearOffset - 1);
  const onNext = () => setYearOffset(yearOffset + 1);

  const isCurrentYear = (y) => y === new Date().getFullYear();

  return (
    <div className="year-view-container">
      <div className="view-years-top-nav">
        <img src={leftarrow}
        title="Past Years"
        alt="Previous"
        className="view-years-nav-arrow"
        onClick={onPrev} />

        {/* Clicking the Years header returns to month view */}
        <div className="main-month" 
        onClick={onToggleYearView} 
        style={{ cursor: "pointer" }}>

          <img src={fontTheme.Years}
          title="Toggle Months"
          alt="Years"
          className="years-header-gif" />
        </div>

        <img src={rightarrow}
        title="Future Years"
        alt="Next"
        className="view-years-nav-arrow"
        onClick={onNext} />
      </div>

      <div className="years-grid">
        {years.map((y) => (
          <div
            key={y}
            className={`year-cell${isCurrentYear(y) ? " year-current" : ""}`}
            onClick={() => onSelectYear(y)}
          >
            <YearDigits year={y} />
          </div>
        ))}
      </div>
    </div>
  );
}