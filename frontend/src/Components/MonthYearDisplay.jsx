import { GraffitiMonths } from "../resources/assets/images/Graffiti_Months/index.js";
import { GraffitiNumbers } from "../resources/assets/images/Graffiti_Numbers/index.js";

import leftarrow from "../resources/assets/images/Signs/Reflective Left Arrow.gif";
import rightarrow from "../resources/assets/images/Signs/Reflective Right Arrow.gif";

const MONTHS = [
  GraffitiMonths.January, GraffitiMonths.February, GraffitiMonths.March,
  GraffitiMonths.April, GraffitiMonths.May, GraffitiMonths.June,
  GraffitiMonths.July, GraffitiMonths.August, GraffitiMonths.September,
  GraffitiMonths.October, GraffitiMonths.November, GraffitiMonths.December,
];

const NUMBERS = [
  GraffitiNumbers.Date0, GraffitiNumbers.Date1, GraffitiNumbers.Date2,
  GraffitiNumbers.Date3, GraffitiNumbers.Date4, GraffitiNumbers.Date5,
  GraffitiNumbers.Date6, GraffitiNumbers.Date7, GraffitiNumbers.Date8,
  GraffitiNumbers.Date9,
];

export default function MonthYearDisplay({ currentDate, onPrev, onNext, viewYears }) {
  return (
    <div className="top-nav">
      <img src={leftarrow}
      title="Last Month"
      alt="Previous"
      className="nav-arrow"
      onClick={onPrev} />

      <div className="main-month">
        <button onClick={viewYears} className="month-year-btn">
          <img src={MONTHS[currentDate.getMonth()]}
            title="Current Month/Toggle Years"
            alt={currentDate.toLocaleString("default", { month: "long" })}
            className="month-gif"
          />
          <div className="year-gifs">
            {String(currentDate.getFullYear()).split("").map((digit, i) => (
              <img
                key={i}
                src={NUMBERS[parseInt(digit)]}
                alt={digit}
                className="number-gif"
              />
            ))}
          </div>
        </button>
      </div>

      <img src={rightarrow}
      title="Next Month"
      alt="Next" className="nav-arrow" 
      onClick={onNext} />
    </div>
  );
}