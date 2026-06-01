import { GraffitiMonths } from "../resources/assets/images/Graffiti_Months/index.js";
import { GraffitiNumbers } from "../resources/assets/images/Graffiti_Numbers/index.js";
import { GraffitiDays } from "../resources/assets/images/Graffiti_Days/index.js";

const MONTHS = [
  GraffitiMonths.January, GraffitiMonths.February, GraffitiMonths.March,
  GraffitiMonths.April, GraffitiMonths.May, GraffitiMonths.June,
  GraffitiMonths.July, GraffitiMonths.August, GraffitiMonths.September,
  GraffitiMonths.October, GraffitiMonths.November, GraffitiMonths.December,
];

const YEAR_DIGITS = [
  GraffitiNumbers.Date0, GraffitiNumbers.Date1, GraffitiNumbers.Date2,
  GraffitiNumbers.Date3, GraffitiNumbers.Date4, GraffitiNumbers.Date5,
  GraffitiNumbers.Date6, GraffitiNumbers.Date7, GraffitiNumbers.Date8,
  GraffitiNumbers.Date9,
];

const DAY_NUMBERS = [
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

const DAY_NUMBERS_INVERTED = [
  GraffitiNumbers.Inverted0,  GraffitiNumbers.Inverted1,  GraffitiNumbers.Inverted2,
  GraffitiNumbers.Inverted3,  GraffitiNumbers.Inverted4,  GraffitiNumbers.Inverted5,
  GraffitiNumbers.Inverted6,  GraffitiNumbers.Inverted7,  GraffitiNumbers.Inverted8,
  GraffitiNumbers.Inverted9,  GraffitiNumbers.Inverted10, GraffitiNumbers.Inverted11,
  GraffitiNumbers.Inverted12, GraffitiNumbers.Inverted13, GraffitiNumbers.Inverted14,
  GraffitiNumbers.Inverted15, GraffitiNumbers.Inverted16, GraffitiNumbers.Inverted17,
  GraffitiNumbers.Inverted18, GraffitiNumbers.Inverted19, GraffitiNumbers.Inverted20,
  GraffitiNumbers.Inverted21, GraffitiNumbers.Inverted22, GraffitiNumbers.Inverted23,
  GraffitiNumbers.Inverted24, GraffitiNumbers.Inverted25, GraffitiNumbers.Inverted26,
  GraffitiNumbers.Inverted27, GraffitiNumbers.Inverted28, GraffitiNumbers.Inverted29,
  GraffitiNumbers.Inverted30, GraffitiNumbers.Inverted31,
];

const DAY_NAMES = [
  GraffitiDays.Sun, GraffitiDays.Mon, GraffitiDays.Tue,
  GraffitiDays.Wed, GraffitiDays.Thu, GraffitiDays.Fri,
  GraffitiDays.Sat
];

const DAY_NAMES_INVERTED = [
  GraffitiDays.SunInverted, GraffitiDays.MonInverted, 
  GraffitiDays.TueInverted, GraffitiDays.WedInverted, GraffitiDays.ThuInverted, 
  GraffitiDays.FriInverted, GraffitiDays.SatInverted 
]

import Remove from "../resources/assets/images/Signs/Red Remove.gif";
import "./CalendarGrid.css";

export default function CalendarGrid({ calendarDays, currentDate, alarms, onDayClick, onDeleteAlarm, onEditAlarm, gridSize, holidays = {} }) {

  function getHoliday(date) 
  {
    if (!date) return null;
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const key = `${year}-${month}-${day}`;
    return holidays[key] ?? null;
  }

  return (
    <div className="calendar-grid" style={{ width: gridSize, height: gridSize }}>

      {DAY_NAMES.map((gif, i) => (
        <div key={i} className="day">
          <img src={gif} alt={DAY_NAMES[i]} className="day-gif" />
        </div>
      ))}

      {calendarDays.map((date, i) => {
        const isToday = date && date.toDateString() === new Date().toDateString();
        const holiday = getHoliday(date);
        return (
          <div
            key={i}
            className={`day-cell ${isToday ? "today" : ""} ${holiday ? "day-cell--holiday" : ""}`}
            onClick={() => onDayClick(date)}
            title={holiday ? holiday.name : undefined}
          >
            {date && (
              <>
                <div className="day-cell-header">
                  {/* Inverted number GIF highlights today's date */}
                  <img
                    src={isToday ? DAY_NUMBERS_INVERTED[date.getDate()] : DAY_NUMBERS[date.getDate()]}
                    alt={date.getDate()}
                    className="day-number-img"
                  />
                  {holiday && (
                    <span className="holiday-label">{holiday.shortName}</span>
                  )}
                </div>
                {/* Filter alarms down to just the ones that belong to this day */}
                {alarms
                  .filter(a => a.time?.startsWith(date.toISOString().split("T")[0]))
                  .map((alarm, idx) => (
                    <div
                      key={idx}
                      className="alarm"
                      onClick={(e) => { e.stopPropagation(); onEditAlarm?.(alarm); }}
                    >
                      <span className="alarm-title">{alarm.title}</span>
                      <span className="alarm-time">
                        {new Date(alarm.time).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                      </span>
                      <img
                        src={Remove}
                        alt="Remove"
                        className="alarm-delete"
                        onClick={(e) => { e.stopPropagation(); onDeleteAlarm(alarm); }}
                      />
                    </div>
                  ))
                }
              </>
            )}
          </div>
        );
      })}
    </div>
  );
}