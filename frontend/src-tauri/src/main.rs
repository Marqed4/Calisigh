#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]
mod backend;
mod commands;
mod java_runtime;
mod logging;
mod ollama;
mod startup;
mod tray;

use tauri::Manager;
use tauri_plugin_autostart::ManagerExt;

pub(crate) const CREATE_NO_WINDOW: u32 = 0x08000000;

fn main() {
    tauri::Builder::default()
        // keep this first, it needs to grab the lock before anything else has a chance to stall
        .plugin(tauri_plugin_single_instance::init(|app, _args, _cwd| {
            tray::show_main_window(app);
        }))
        .plugin(tauri_plugin_autostart::Builder::new().args(vec!["--autostart"]).build())
        .plugin(tauri_plugin_opener::init())
        .plugin(tauri_plugin_dialog::init())
        .setup(|app| {
            tray::build(app)?;

            let autolaunch = app.autolaunch();
            let _ = autolaunch.enable();

            let is_autostart = std::env::args().any(|a| a == "--autostart");
            if let Some(win) = app.get_webview_window("main") {
                if is_autostart {
                    let _ = win.set_skip_taskbar(true);
                    let _ = win.hide();
                } else {
                    let _ = win.set_skip_taskbar(false);
                    let _ = win.show();
                    let _ = win.set_focus();
                }
            }

            /*
                Autostart issue explanation:

                The backend environment checks (Ollama, Java, and other startup probes)
                can take a long time or even hang right after system boot. Previously,
                `setup()` waited for all of these checks to finish before returning.

                Tauri does not begin processing window events until `setup()` completes.
                Because of that, the single‑instance handler never received the relaunch
                message sent by a second app invocation. The second launch would then
                block forever waiting for a reply that never arrived, and each retry
                created an additional zombie process.

                The fix is to move all slow initialization work (see `startup.rs`) into
                a separate thread so `setup()` returns immediately. This allows Tauri to
                start pumping window messages right away, ensuring the single‑instance
                lock and relaunch behavior work correctly.
            */
            
            let app_handle = app.handle().clone();
            std::thread::spawn(move || startup::run(app_handle, is_autostart));

            Ok(())
        })
        .on_window_event(|window, event| {
            match event {
                tauri::WindowEvent::CloseRequested { api, .. } => {
                    if window.label() == "main" {
                        api.prevent_close();
                        let _ = window.set_skip_taskbar(true);
                        let _ = window.hide();
                    }
                }
                tauri::WindowEvent::Destroyed => {
                    if window.label() == "main" {
                        backend::kill();
                    }
                }
                _ => {}
            }
        })
        .invoke_handler(tauri::generate_handler![commands::call_java])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
