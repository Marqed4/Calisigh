use std::os::windows::process::CommandExt;
use std::process::{Command, Stdio};

use crate::logging::log_line;
use crate::CREATE_NO_WINDOW;

fn pid_file() -> std::path::PathBuf {
    std::env::temp_dir().join("calisigh-backend.pid")
}

pub fn find_jar(exe_dir: &std::path::Path) -> Option<std::path::PathBuf> {
    exe_dir
        .ancestors()
        .find_map(|p| {
            let candidate = p.join("CustomCalendar.jar");
            if candidate.exists() { Some(candidate) } else { None }
        })
        .or_else(|| {
            let candidate = exe_dir.join("CustomCalendar.jar");
            if candidate.exists() { Some(candidate) } else { None }
        })
}

// used to shell out to wmic to check if CustomCalendar.jar was already running. checked and
// wmic.exe doesn't even exist on this machine anymore (removed in newer Windows 11 builds), so
// that check was silently always returning false instead of hanging like first assumed.
// tracking our own backend's pid ourselves and checking it with tasklist instead, no WMI needed.
pub fn is_running() -> bool {
    let pid = match std::fs::read_to_string(pid_file()) {
        Ok(s) => match s.trim().parse::<u32>() {
            Ok(pid) => pid,
            Err(_) => return false,
        },
        Err(_) => return false,
    };

    Command::new("tasklist")
        .args(["/FI", &format!("PID eq {}", pid), "/NH"])
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .map(|o| String::from_utf8_lossy(&o.stdout).contains(&pid.to_string()))
        .unwrap_or(false)
}

pub fn spawn(jar_path: &std::path::Path) {
    #[cfg(debug_assertions)]
    let result = {
        let mut child = Command::new("java")
            .arg("-jar")
            .arg(jar_path)
            .stdin(Stdio::null())
            .stdout(Stdio::piped())
            .stderr(Stdio::piped())
            .creation_flags(CREATE_NO_WINDOW)
            .spawn();

        if let Ok(ref mut child) = child {
            if let Some(stdout) = child.stdout.take() {
                std::thread::spawn(move || {
                    use std::io::{BufRead, BufReader};
                    for line in BufReader::new(stdout).lines() {
                        if let Ok(line) = line {
                            println!("[java] {}", line);
                        }
                    }
                });
            }
            if let Some(stderr) = child.stderr.take() {
                std::thread::spawn(move || {
                    use std::io::{BufRead, BufReader};
                    for line in BufReader::new(stderr).lines() {
                        if let Ok(line) = line {
                            eprintln!("[java:err] {}", line);
                        }
                    }
                });
            }
        }
        child
    };

    #[cfg(not(debug_assertions))]
    let result = Command::new("java")
        .arg("-jar")
        .arg(jar_path)
        .stdin(Stdio::null())
        .stdout(Stdio::null())
        .stderr(Stdio::null())
        .creation_flags(CREATE_NO_WINDOW)
        .spawn();

    match &result {
        Ok(child) => {
            log_line(&format!("Backend started, pid {}", child.id()));
            let _ = std::fs::write(pid_file(), child.id().to_string());
        }
        Err(e) => log_line(&format!("backend won't start. everything's ruined. {}", e)),
    }
}

pub fn kill() {
    if let Ok(s) = std::fs::read_to_string(pid_file()) {
        if let Ok(pid) = s.trim().parse::<u32>() {
            let _ = Command::new("taskkill")
                .args(["/F", "/PID", &pid.to_string()])
                .creation_flags(CREATE_NO_WINDOW)
                .spawn();
            return;
        }
    }
    // no pid on record, fall back to the old blanket kill so nothing gets left behind
    let _ = Command::new("cmd")
        .args(["/C", "taskkill /F /IM java.exe"])
        .creation_flags(CREATE_NO_WINDOW)
        .spawn();
}
