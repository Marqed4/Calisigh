use tauri::{
    menu::{Menu, MenuItem, PredefinedMenuItem},
    tray::{MouseButton, MouseButtonState, TrayIconBuilder, TrayIconEvent},
    Manager,
};
use tauri_plugin_autostart::ManagerExt;
use tauri_plugin_dialog::DialogExt;

use crate::backend;

/*
UNDERSTAND: Right after cold boot the webview may not have finished registering yet.

PLAN: Attempt to show the main window, retrying up to 10 times with 200ms gaps.
*/
pub fn show_main_window(app: &tauri::AppHandle) {
    let app = app.clone();
    std::thread::spawn(move || {
        for _ in 0..10 {
            if let Some(win) = app.get_webview_window("main") {
                let _ = win.set_skip_taskbar(false);
                let _ = win.show();
                let _ = win.set_focus();
                return;
            }
            std::thread::sleep(std::time::Duration::from_millis(200));
        }
        #[cfg(debug_assertions)]
        println!("show_main_window: window never showed up. 10 tries. i give up.");
    });
}

pub fn build(app: &tauri::App) -> tauri::Result<()> {
    let show_item = MenuItem::with_id(app, "show", "Show", true, None::<&str>)?;
    let toggle_autostart_item = MenuItem::with_id(app, "toggle_autostart", "Toggle Autostart", true, None::<&str>)?;
    let quit_item = MenuItem::with_id(app, "quit", "Quit", true, None::<&str>)?;
    let sep1 = PredefinedMenuItem::separator(app)?;
    let sep2 = PredefinedMenuItem::separator(app)?;

    let tray_menu = Menu::with_items(app, &[
        &show_item,
        &sep1,
        &toggle_autostart_item,
        &sep2,
        &quit_item,
    ])?;

    let _tray = TrayIconBuilder::new()
        .icon({
            /*
            UNDERSTAND: was using env!("CARGO_MANIFEST_DIR") here which just hardcodes
            my dev folder path into the binary lol.

            PLAN: embed the icon bytes instead so it actually works once installed
            somewhere else.
            */
            let img = image::load_from_memory(include_bytes!("../icons/128x128@2x.png"))
                .expect("bundled tray icon is corrupt")
                .into_rgba8();
            let (w, h) = img.dimensions();
            tauri::image::Image::new_owned(img.into_raw(), w, h)
        })
        .menu(&tray_menu)
        .on_menu_event(|app, event| match event.id.as_ref() {
            "show" => {
                show_main_window(app);
            }

            "toggle_autostart" => {
                let autolaunch = app.autolaunch();
                match autolaunch.is_enabled() {
                    Ok(true) => { let _ = autolaunch.disable(); }
                    Ok(false) => { let _ = autolaunch.enable(); }
                    Err(e) => {
                        let _ = app
                            .dialog()
                            .message(&format!("couldn't even toggle autostart. of course.\n\n{}", e))
                            .title("autostart's broken too")
                            .blocking_show();
                    }
                }
            }
            "quit" => {
                backend::kill();
                app.exit(0);
            }
            _ => {}
        })
        .on_tray_icon_event(|tray, event| {
            if let TrayIconEvent::Click {
                button: MouseButton::Left,
                button_state: MouseButtonState::Up,
                ..
            } = event {
                show_main_window(tray.app_handle());
            }
        })
        .build(app)?;

    Ok(())
}
