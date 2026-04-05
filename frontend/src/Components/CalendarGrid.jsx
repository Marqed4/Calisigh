import sun from "../resources/assets/images/Sun Inverted.gif";
import mon from "../resources/assets/images/Mon Inverted.gif";
import tue from "../resources/assets/images/Tue Inverted.gif";
import wed from "../resources/assets/images/Wed Inverted.gif";
import thu from "../resources/assets/images/Thu Inverted.gif";
import fri from "../resources/assets/images/Fri Inverted.gif";
import sat from "../resources/assets/images/Sat Inverted.gif";
import "./CalendarGrid.css";

const DAY_GIFS = [sun, mon, tue, wed, thu, fri, sat];
const DAY_NAMES = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];

export default function CalendarGrid({ calendarDays, currentDate, alarms, onDayClick }) {
  return (
    <div className="calendar-grid">

      {/* Day header GIFs */}
      {DAY_GIFS.map((gif, i) => (
        <div key={i} className="day-label">
          <img src={gif} alt={DAY_NAMES[i]} className="day-label-gif" />
        </div>
      ))}

      {/* Day cells */}
      {calendarDays.map((date, i) => (
        <div
          key={i}
          className={`day-cell ${
            date && date.toDateString() === new Date().toDateString() ? "today" : ""
          }`}
          onClick={() => onDayClick(date)}
        >
          {date && (
            <>
              <div className="day-number">{date.getDate()}</div>
              {alarms.some(a =>
                a.time?.startsWith(date.toISOString().split("T")[0])
              ) && <div className="alarm-dot" />}
            </>
          )}
        </div>
      ))}

    </div>
  );
}
