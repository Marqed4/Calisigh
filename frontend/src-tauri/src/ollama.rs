use std::os::windows::process::CommandExt;
use std::process::Command;

use crate::CREATE_NO_WINDOW;

pub fn is_ollama_installed() -> bool {
    Command::new("ollama")
        .arg("--version")
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .is_ok()
}

pub fn is_model_available() -> bool {
    Command::new("ollama")
        .args(["list"])
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .map(|o| String::from_utf8_lossy(&o.stdout).contains("llama3.2"))
        .unwrap_or(false)
}

pub fn pull_model_in_background() {
    std::thread::spawn(|| {
        let _ = Command::new("ollama")
            .args(["pull", "llama3.2"])
            .creation_flags(CREATE_NO_WINDOW)
            .status();
    });
}

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
