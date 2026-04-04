const BASE = "http://localhost:4567/api";

export async function getAlarms() {
  const res = await fetch(`${BASE}/alarms`);
  return res.json();
}

export async function createAlarm(time, title, desc) {
  const res = await fetch(`${BASE}/alarms`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ time, title, desc }),
  });
  return res.json();
}