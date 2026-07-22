use std::io::Write;

// release builds have no console (windows_subsystem = "windows" in main.rs), so println! goes
// nowhere. this is the only way to see what actually happened on a real boot instead of
// guessing again. appends to %TEMP%\calisigh-startup.log, always on, both debug and release.
pub fn log_line(msg: &str) {
    let secs = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .map(|d| d.as_secs())
        .unwrap_or(0);
    if let Ok(mut f) = std::fs::OpenOptions::new()
        .create(true)
        .append(true)
        .open(std::env::temp_dir().join("calisigh-startup.log"))
    {
        let _ = writeln!(f, "[{}] {}", secs, msg);
    }
    #[cfg(debug_assertions)]
    println!("{}", msg);
}
