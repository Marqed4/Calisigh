#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

mod commands;

use std::process::{Command, Stdio};

fn main() {
    let exe_dir = std::env::current_exe()
        .ok()
        .and_then(|p| p.parent().map(|p| p.to_path_buf()))
        .unwrap_or_else(|| std::path::PathBuf::from("."));

    Command::new("java")
        .arg("-jar")
        .arg("CustomCalendar.jar")
        .current_dir(&exe_dir)
        .stdin(Stdio::null())
        .stdout(Stdio::null())
        .stderr(Stdio::null())
        .spawn()
        .expect("Failed to start Java backend");

    tauri::Builder::default()
        .invoke_handler(tauri::generate_handler![
            commands::call_java,
            commands::open_alarm_window
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}