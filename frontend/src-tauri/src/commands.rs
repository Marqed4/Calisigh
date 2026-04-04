use std::process::{Command, Stdio};
use std::io::Write;
use tauri::{AppHandle, Manager, WebviewWindowBuilder, WebviewUrl};

#[tauri::command]
pub async fn call_java(json: String) -> Result<String, String> {
    let mut child = Command::new("java")
        .arg("-jar")
        .arg("CustomCalendar.jar")
        .stdin(Stdio::piped())
        .stdout(Stdio::piped())
        .spawn()
        .map_err(|e| e.to_string())?;

    if let Some(stdin) = &mut child.stdin {
        stdin.write_all(json.as_bytes()).unwrap();
    }

    let output = child.wait_with_output().unwrap();
    let response = String::from_utf8(output.stdout).unwrap();

    Ok(response)
}

#[tauri::command]
pub async fn open_alarm_window(app: AppHandle, day: u32, month: u32, year: i32) -> Result<(), String> {
    let label = "add-alarm";
    let url = format!("/add-alarm?day={}&month={}&year={}", day, month, year);

    // Close existing window if open
    if let Some(existing) = app.get_webview_window(label) {
        existing.close().map_err(|e: tauri::Error| e.to_string())?;
        // Wait for window to close
        tokio::time::sleep(std::time::Duration::from_millis(300)).await;
    }

    WebviewWindowBuilder::new(&app, label, WebviewUrl::App(url.into()))
        .title("Add Alarm")
        .inner_size(420.0, 420.0)
        .resizable(false)
        .devtools(true)
        .build()
        .map_err(|e| e.to_string())?;

    Ok(())
}