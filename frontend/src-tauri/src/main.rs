#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

mod commands;

use std::process::{Command, Stdio};
use tauri::Manager;
use tauri_plugin_dialog::DialogExt;

fn is_ollama_installed() -> bool {
    Command::new("ollama").arg("--version").output().is_ok()
}

fn is_model_available() -> bool {
    Command::new("ollama")
        .args(["list"])
        .output()
        .map(|o| String::from_utf8_lossy(&o.stdout).contains("llama3.2"))
        .unwrap_or(false)
}

fn main() {
    tauri::Builder::default()
        .plugin(tauri_plugin_single_instance::init(|app, _args, _cwd| {
            if let Some(window) = app.get_webview_window("main") {
                let _ = window.set_focus();
            }
        }))
        .plugin(tauri_plugin_opener::init())
        .plugin(tauri_plugin_dialog::init())
        .setup(|app| {
            if !is_ollama_installed() {
                app.dialog()
                    .message("Calisigh 3.2 requires Ollama for its chat assistant.\n\nInstall it from https://ollama.com/download, then run:\n\n  ollama pull llama3.2")
                    .title("Ollama 3.2 Required")
                    .blocking_show();
            } else if !is_model_available() {
                std::thread::spawn(|| {
                    let _ = Command::new("ollama").args(["pull", "llama3.2"]).status();
                });
            }

            println!("Ollama is installed!");

            let exe_dir = std::env::current_exe()
                .ok()
                .and_then(|p| p.parent().map(|p| p.to_path_buf()))
                .unwrap_or_else(|| std::path::PathBuf::from("."));

            println!("exe_dir: {:?}", exe_dir);

            let jar_path = exe_dir
                .ancestors()
                .find_map(|p| {
                    let candidate = p.join("CustomCalendar.jar");
                    if candidate.exists() { Some(candidate) } else { None }
                })
                .expect("Could not find CustomCalendar.jar");

            println!("Found JAR at: {:?}", jar_path);

            let already_running = Command::new("cmd")
                .args(["/C", "tasklist | findstr CustomCalendar.jar"])
                .output()
                .map(|o| !o.stdout.is_empty())
                .unwrap_or(false);

            if !already_running {
                let result = Command::new("java")
                    .arg("-jar")
                    .arg(&jar_path)
                    .stdin(Stdio::null())
                    .stdout(Stdio::inherit())
                    .stderr(Stdio::inherit())
                    .spawn();

                match result {
                    Ok(_) => println!("Backend started!"),
                    Err(e) => println!("Failed: {}", e),
                }
            } else {
                println!("Backend already running, skipping.");
            }

            Ok(())
        })
        .invoke_handler(tauri::generate_handler![commands::call_java])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}