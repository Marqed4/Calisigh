import { useState, useEffect, useRef } from "react";
import { getCurrentWebviewWindow } from "@tauri-apps/api/webviewWindow";
import "./Chat.css";

const appWindow = getCurrentWebviewWindow();

export default function Chat() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [alarms, setAlarms] = useState([]);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    async function init() {
      try {
        const res = await fetch("http://localhost:4567/api/alarms");
        const data = await res.json();
        setAlarms(data);

        const topics = data.map(a => `"${a.title}" on ${new Date(a.time).toLocaleDateString()}`);
        const greeting = topics.length > 0
          ? "Hey... I can see what you're up to, what do you want?"
          : "Hey... looks like you have no plans. Who would've guessed. Feel free to talk to me anyway.";

        setMessages([{ role: "assistant", content: greeting }]);
      } catch (err) {
        setMessages([{ role: "assistant", content: 
            "Hey... looks like you have no plans. Who would've guessed. Feel free to talk to me anyway." }]);
      }
    }
    init();
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const sendMessage = async () => {
    if (!input.trim() || loading) return;

    const userMsg = { role: "user", content: input.trim() };
    const newMessages = [...messages, userMsg];
    setMessages(newMessages);
    setInput("");
    setLoading(true);

    try {
      const alarmContext = alarms.length > 0
        ? `The user's calendar events are: ${alarms.map(a =>
            `"${a.title}" at ${new Date(a.time).toLocaleString()}${a.desc ? ` (${a.desc})` : ""}`
          ).join("; ")}.`
        : "The user has no calendar events.";

      const response = await fetch("http://localhost:4567/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          alarmContext,
          messages: newMessages,
        }),
      });

      const data = await response.json();
      const reply = data.reply ?? "...whatever, I got nothing. Try again I guess.";
      setMessages(prev => [...prev, { role: "assistant", content: reply }]);
    } catch (err) {
      setMessages(prev => [...prev, { role: "assistant", content: "Great, something broke. not surprised honestly. Try again." }]);
    }

    setLoading(false);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  return (
    <div className="chat-wrapper">
      <div className="chat-header">
        <span>Calisigh Helper</span>
        <button className="chat-close" onClick={() => appWindow.close()}>✕</button>
      </div>

      <div className="chat-messages">
        {messages.map((msg, i) => (
          <div key={i} className={`chat-bubble ${msg.role}`}>
            {msg.content}
          </div>
        ))}
        {loading && (
          <div className="chat-bubble assistant loading">
            <span className="dot" /><span className="dot" /><span className="dot" />
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input-row">
        <textarea
          className="chat-input"
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Ask about your events..."
          rows={2}
        />
        <button className="chat-send" onClick={sendMessage} disabled={loading}>
          ➤
        </button>
      </div>
    </div>
  );
}