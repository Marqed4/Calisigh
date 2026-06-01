import { useState } from "react";
import "./ViewYears.css";

import { GraffitiNumbers } from "../resources/assets/images/Graffiti_Numbers/index.js";

const YEAR_DIGITS = [
  GraffitiNumbers.Date0, GraffitiNumbers.Date1, GraffitiNumbers.Date2,
  GraffitiNumbers.Date3, GraffitiNumbers.Date4, GraffitiNumbers.Date5,
  GraffitiNumbers.Date6, GraffitiNumbers.Date7, GraffitiNumbers.Date8,
  GraffitiNumbers.Date9,
];

import Years from "../resources/assets/images/Signs/Years.gif";

import leftarrow from "../resources/assets/images/Signs/Reflective Left Arrow.gif";
import rightarrow from "../resources/assets/images/Signs/Reflective Right Arrow.gif";

function YearDigits({ year, className }) {
  return (
    <div className="view-years-cell-gifs">
      {String(year).split("").map((digit, i) => (
        <img
          key={i}
          src={YEAR_DIGITS[parseInt(digit)]}
          alt={digit}
          className={"view-years-number-gif"}
        />
      ))}
    </div>
  );
}

export default function ViewYears({ currentDate, onToggleYearView, onSelectYear }) {
  const [yearOffset, setYearOffset] = useState(0);

  const YEARS_PER_PAGE = 35;
  const START_YEAR = new Date().getFullYear();

  const start = START_YEAR + yearOffset * YEARS_PER_PAGE;
  const end = start + YEARS_PER_PAGE - 1;

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
          title = "Past Years"
          alt="Previous" className="view-years-nav-arrow" onClick={onPrev} />

          <div className="main-month" onClick={onToggleYearView} style={{ cursor: "pointer" }}>
            <img src={Years}
            title = "Toggle Months"
            alt="Years" 
            className="years-header-gif" />
          </div>

          <img src={rightarrow}
          title = "Future Years"
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