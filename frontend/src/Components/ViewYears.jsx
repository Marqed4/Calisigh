import { useState } from "react";
import "./ViewYears.css";

import n0 from "../resources/assets/images/Numbers/0.gif";
import n1 from "../resources/assets/images/Numbers/1.gif";
import n2 from "../resources/assets/images/Numbers/2.gif";
import n3 from "../resources/assets/images/Numbers/3.gif";
import n4 from "../resources/assets/images/Numbers/4.gif";
import n5 from "../resources/assets/images/Numbers/5.gif";
import n6 from "../resources/assets/images/Numbers/6.gif";
import n7 from "../resources/assets/images/Numbers/7.gif";
import n8 from "../resources/assets/images/Numbers/8.gif";
import n9 from "../resources/assets/images/Numbers/9.gif";

import Years from "../resources/assets/images/ShapesSigns/Years.gif";

import leftarrow from "../resources/assets/images/ShapesSigns/Reflective Left Arrow.gif";
import rightarrow from "../resources/assets/images/ShapesSigns/Reflective Right Arrow.gif";

const NUMBERS = [n0, n1, n2, n3, n4, n5, n6, n7, n8, n9];

function YearDigits({ year, className }) {
  return (
    <div className="view-years-cell-gifs">
      {String(year).split("").map((digit, i) => (
        <img
          key={i}
          src={NUMBERS[parseInt(digit)]}
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
          <img src={leftarrow} alt="Previous" className="view-years-nav-arrow" onClick={onPrev} />

          <div className="main-month" onClick={onToggleYearView} style={{ cursor: "pointer" }}>
            <img src={Years} alt="Years" className="years-header-gif" />
          </div>

          <img src={rightarrow} alt="Next" className="view-years-nav-arrow" onClick={onNext} />
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