use std::os::windows::process::CommandExt;
use std::process::Command;

use crate::logging::log_line;
use crate::CREATE_NO_WINDOW;

pub fn is_java_installed() -> bool {
    Command::new("java")
        .arg("-version")
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .is_ok()
}

pub fn install_jre(exe_dir: &std::path::Path) -> Result<(), String> {
    let msi_path = exe_dir
        .ancestors()
        .find_map(|p| {
            let candidate = p.join("temurin-21-jre.msi");
            if candidate.exists() { Some(candidate) } else { None }
        })
        .ok_or_else(|| "Could not find temurin-21-jre.msi in bundle".to_string())?;

    log_line(&format!("Installing JRE from: {:?}", msi_path));

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
