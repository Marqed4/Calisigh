use std::os::windows::process::CommandExt;
use std::process::{Command, Stdio};

use crate::logging::log_line;
use crate::CREATE_NO_WINDOW;

/*
UNDERSTAND: Without nulling stdin, spawning a child from an autostart-launched process
can hang forever waiting on a stdin handle that doesn't exist yet at that point in the
logon sequence. See the comment at the top of ollama.rs for why.

PLAN: stdin explicitly nulled on every Command below.
*/

pub fn is_java_installed() -> bool {
    Command::new("java")
        .arg("-version")
        .stdin(Stdio::null())
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
        .stdin(Stdio::null())
        .creation_flags(CREATE_NO_WINDOW)
        .status()
        .map_err(|e| format!("Failed to launch msiexec: {}", e))?;

    if status.success() {
        Ok(())
    } else {
        Err(format!("msiexec exited with status: {}", status))
    }
}