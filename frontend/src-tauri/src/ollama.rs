use std::os::windows::process::CommandExt;
use std::process::{Command, Stdio};

use crate::CREATE_NO_WINDOW;

/*
UNDERSTAND: Command inherits the parent's stdin handle by default, and on autostart
(launched via the Run key at logon) there's no valid handle to inherit yet. CreateProcess
can then hang indefinitely spawning a console-subsystem child (ollama.exe) waiting on
that. Launched interactively this isn't an issue since a real handle exists to inherit.
Without Stdio::null() here, this whole setup thread can block forever on the very first
call, which also stops java/the backend from ever getting checked or started, so this
one fix covers both symptoms.

EDGE CASES: None when launched interactively, a real stdin handle exists to inherit.

PLAN: Every Command below explicitly nulls stdin.
*/

pub fn is_ollama_installed() -> bool {
    Command::new("ollama")
        .arg("--version")
        .stdin(Stdio::null())
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .is_ok()
}

pub fn is_model_available() -> bool {
    Command::new("ollama")
        .args(["list"])
        .stdin(Stdio::null())
        .creation_flags(CREATE_NO_WINDOW)
        .output()
        .map(|o| String::from_utf8_lossy(&o.stdout).contains("llama3.2"))
        .unwrap_or(false)
}

pub fn pull_model_in_background() {
    std::thread::spawn(|| {
        let _ = Command::new("ollama")
            .args(["pull", "llama3.2"])
            .stdin(Stdio::null())
            .creation_flags(CREATE_NO_WINDOW)
            .status();
    });
}
