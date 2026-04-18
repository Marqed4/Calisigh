#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]
mod commands;
use std::os::windows::process::CommandExt;
use std::process::{Command, Stdio};
use tauri::Manager;
use tauri_plugin_dialog::DialogExt;

const CREATE_NO_WINDOW: u32 = 0x08000000;

fn install_ollama() -> Result<(), String> {
    let status = Command::new("powershell")
        .args([
            "-NoProfile",
            "-NonInteractive", 
            "-Command",
            "Invoke-WebRequest -Uri 'https://ollama.com/download/OllamaSetup.exe' -OutFile \"$env:TEMP\\OllamaSetup.exe\"; Start-Process \"$env:TEMP\\OllamaSetup.exe\" -Wait"
        ])
        .creation_flags(CREATE_NO_WINDOW)
        .status()
        .map_err(|e| e.to_string())?;

    if status.success() {
        Ok(())
    } else {
        Err("Ollama installer failed".to_string())
    }
}

fn is_ollama_installed() -> bool {
    Command::new("ollama")
        .arg("--version")
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .is_ok()
}

fn is_model_available() -> bool {
    Command::new("ollama")
        .args(["list"])
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .map(|o| String::from_utf8_lossy(&o.stdout).contains("llama3.2"))
        .unwrap_or(false)
}

fn is_java_installed() -> bool {
    Command::new("java")
        .arg("-version")
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .is_ok()
}

fn install_jre(exe_dir: &std::path::Path) -> Result<(), String> {
    let msi_path = exe_dir
        .ancestors()
        .find_map(|p| {
            let candidate = p.join("temurin-21-jre.msi");
            if candidate.exists() { Some(candidate) } else { None }
        })
        .ok_or_else(|| "Could not find temurin-21-jre.msi in bundle".to_string())?;

    #[cfg(debug_assertions)]
    println!("Installing JRE from: {:?}", msi_path);

    let status = Command::new("msiexec")
        .args([
            "/i",
            msi_path.to_str().unwrap(),
            "/quiet",
            "/norestart",
        ])
        .creation_flags(CREATE_NO_WINDOW)
        .status()
        .map_err(|e| format!("Failed to launch msiexec: {}", e))?;

    if status.success() {
        Ok(())
    } else {
        Err(format!("msiexec exited with status: {}", status))
    }
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
            // Ollama check
            if !is_ollama_installed() {
                app.dialog()
                    .message("Calisigh 3.2 requires Ollama for its chat assistant.\n\nInstall it from https://ollama.com/download, then run:\n\n  ollama pull llama3.2")
                    .title("Ollama 3.2 Required")
                    .blocking_show();
            } else if !is_model_available() {
                std::thread::spawn(|| {
                    let _ = Command::new("ollama")
                        .args(["pull", "llama3.2"])
                        .creation_flags(CREATE_NO_WINDOW)
                        .status();
                });
            }

            let exe_dir = std::env::current_exe()
                .ok()
                .and_then(|p| p.parent().map(|p| p.to_path_buf()))
                .unwrap_or_else(|| std::path::PathBuf::from("."));

            #[cfg(debug_assertions)]
            println!("exe_dir: {:?}", exe_dir);

            if !is_java_installed() {
                app.dialog()
                    .message("Java is not installed. Calisigh will now install the bundled Java 21 runtime.\n\nThis may take a minute…")
                    .title("Installing Java Runtime")
                    .blocking_show();

                match install_jre(&exe_dir) {
                    Ok(_) => {
                        #[cfg(debug_assertions)]
                        println!("JRE installed successfully.");
                        app.dialog()
                            .message("Java 21 was installed successfully.")
                            .title("Java Installed")
                            .blocking_show();
                    }
                    Err(e) => {
                        app.dialog()
                            .message(&format!(
                                "Failed to install Java automatically:\n\n{}\n\nPlease install Java 21 manually from https://adoptium.net",
                                e
                            ))
                            .title("Java Install Failed")
                            .blocking_show();
                        std::process::exit(1);
                    }
                }
            }

            let jar_path = exe_dir
                .ancestors()
                .find_map(|p| {
                    let candidate = p.join("CustomCalendar.jar");
                    if candidate.exists() { Some(candidate) } else { None }
                })
                .expect("Could not find CustomCalendar.jar");

            #[cfg(debug_assertions)]
            println!("Found JAR at: {:?}", jar_path);

            let already_running = Command::new("cmd")
                .args(["/C", "tasklist | findstr CustomCalendar.jar"])
                .creation_flags(CREATE_NO_WINDOW)
                .output()
                .map(|o| !o.stdout.is_empty())
                .unwrap_or(false);

            if !already_running {
                let result = Command::new("java")
                    .arg("-jar")
                    .arg(&jar_path)
                    .stdin(Stdio::null())
                    .stdout(Stdio::null())
                    .stderr(Stdio::null())
                    .creation_flags(CREATE_NO_WINDOW)
                    .spawn();

                #[cfg(debug_assertions)]
                match result {
                    Ok(_) => println!("Backend started!"),
                    Err(e) => println!("Failed: {}", e),
                }
                #[cfg(not(debug_assertions))]
                let _ = result;
            } else {
                #[cfg(debug_assertions)]
                println!("Backend already running, skipping.");
            }

            Ok(())
        })
        .on_window_event(|window, event| {
            if let tauri::WindowEvent::Destroyed = event {
                if window.label() == "main" {
                    let _ = Command::new("cmd")
                        .args(["/C", "taskkill /F /IM java.exe"])
                        .creation_flags(CREATE_NO_WINDOW)
                        .spawn();
                }
            }
        })
        .invoke_handler(tauri::generate_handler![commands::call_java])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}