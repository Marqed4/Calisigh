use tauri_plugin_dialog::DialogExt;

use crate::backend;
use crate::java_runtime;
use crate::logging::log_line;
use crate::ollama;

// TODO: Also check clear  toast notifications on auto start.

// Everything here talks to external processes (ollama, java, msiexec) and can block for a
// while, especially right after a cold boot. runs on its own thread see main.rs's setup()
// for why it can't run inline there.
pub fn run(app_handle: tauri::AppHandle, is_autostart: bool) {
    log_line(&format!("--- setup thread starting, autostart={} ---", is_autostart));

    check_ollama(&app_handle, is_autostart);

    let exe_dir = std::env::current_exe()
        .ok()
        .and_then(|p| p.parent().map(|p| p.to_path_buf()))
        .unwrap_or_else(|| std::path::PathBuf::from("."));
    log_line(&format!("exe_dir: {:?}", exe_dir));

    if !ensure_java(&app_handle, &exe_dir, is_autostart) {
        return;
    }

    let jar_path = match backend::find_jar(&exe_dir) {
        Some(p) => p,
        None => {
            log_line("can't find CustomCalendar.jar anywhere. giving up. not like it matters.");
            return;
        }
    };
    log_line(&format!("Found JAR at: {:?}", jar_path));

    let already_running = backend::is_running();
    log_line(&format!("already_running: {}", already_running));

    if already_running {
        log_line("Backend already running, skipping.");
        return;
    }

    backend::spawn(&jar_path);
}

fn check_ollama(app_handle: &tauri::AppHandle, is_autostart: bool) {
    if !ollama::is_ollama_installed() {
        log_line("ollama not found on PATH");
        if is_autostart {
            log_line("autostart run, skipping blocking dialog for missing ollama");
        } else {
            app_handle
                .dialog()
                .message("whatever. Calisigh needs Ollama for the chat thing and it's just not here. typical.\n\nget it from https://ollama.com/download, then run:\n\n  ollama pull llama3.2\n\nnobody ever explains anything to me either.")
                .title("ollama's missing. so is everything else.")
                .blocking_show();
        }
    } else if !ollama::is_model_available() {
        log_line("ollama found but llama3.2 model missing, pulling in background");
        ollama::pull_model_in_background();
    } else {
        log_line("ollama + llama3.2 OK");
    }
}

fn ensure_java(app_handle: &tauri::AppHandle, exe_dir: &std::path::Path, is_autostart: bool) -> bool {
    if java_runtime::is_java_installed() {
        return true;
    }

    log_line("java not found on PATH, installing bundled JRE");
    if !is_autostart {
        app_handle
            .dialog()
            .message("no Java installed. fine. I'll do it myself, like always.\n\ninstalling the bundled Java 21 runtime now. this'll take a minute. don't wait up.")
            .title("installing java. don't ask.")
            .blocking_show();
    }

    match java_runtime::install_jre(exe_dir) {
        Ok(_) => {
            log_line("JRE installed successfully");
            if !is_autostart {
                app_handle
                    .dialog()
                    .message("Java 21 installed. it worked. weird.")
                    .title("java's in. whatever.")
                    .blocking_show();
            }
            true
        }
        Err(e) => {
            log_line(&format!("jre install failed. of course it did. {}", e));
            if is_autostart {
                /*
                Don't exit(1) silently on autostart that kills the whole app
                with no explanation visible to the user. Just bail on backend
                setup and let them find out (and fix it) from a normal launch.
                */
                log_line("autostart run: skipping failure dialog, leaving backend down");
                false
            } else {
                app_handle
                    .dialog()
                    .message(&format!(
                        "of course it failed:\n\n{}\n\nguess you'll have to install Java 21 yourself from https://adoptium.net. figures.",
                        e
                    ))
                    .title("java install failed. nothing works.")
                    .blocking_show();
                std::process::exit(1);
            }
        }
    }
}
