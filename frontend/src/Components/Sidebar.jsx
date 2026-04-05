import "./Sidebar.css";

export default function Sidebar({ currentDate, calendarDays }) {
  return (
    <aside className="sidebar">
      <h2 className="sidebar-month">
        {currentDate.toLocaleString("default", { month: "long" })}{" "}
        {currentDate.getFullYear()}
      </h2>

      <div className="mini-grid">
        {["S","M","T","W","T","F","S"].map((d, i) => (
          <div key={i} className="mini-day-label">{d}</div>
        ))}
        {calendarDays.map((date, i) => (
          <div key={i} className="mini-day">
            {date?.getDate()}
          </div>
        ))}
      </div>
    </aside>
  );
}
