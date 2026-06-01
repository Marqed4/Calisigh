import { useFontTheme } from "./FontPackage.jsx";
import leftarrow from "../resources/assets/images/Signs/Reflective Left Arrow.gif";
import rightarrow from "../resources/assets/images/Signs/Reflective Right Arrow.gif";

export default function MonthYearDisplay({ currentDate, onPrev, onNext, viewYears }) {

  /* Pull active font assets from context */
  const { fontTheme }   = useFontTheme();
  const GraffitiMonths  = fontTheme.Months;
  const GraffitiNumbers = fontTheme.Numbers;

  /* Built inside the function so they reflect the current font theme */

  const MONTHS = [
    fontTheme.Months.January,   fontTheme.Months.February, fontTheme.Months.March,
    fontTheme.Months.April,     fontTheme.Months.May,      fontTheme.Months.June,
    fontTheme.Months.July,      fontTheme.Months.August,   fontTheme.Months.September,
    fontTheme.Months.October,   fontTheme.Months.November, fontTheme.Months.December,
  ];

  const NUMBERS = [
    fontTheme.Numbers.Date0, fontTheme.Numbers.Date1, fontTheme.Numbers.Date2,
    fontTheme.Numbers.Date3, fontTheme.Numbers.Date4, fontTheme.Numbers.Date5,
    fontTheme.Numbers.Date6, fontTheme.Numbers.Date7, fontTheme.Numbers.Date8,
    fontTheme.Numbers.Date9,
  ];

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
      alt="Next"
      className="nav-arrow"
      onClick={onNext} />
    </div>
  );
}