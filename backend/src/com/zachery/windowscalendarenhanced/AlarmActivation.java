package com.zachery.windowscalendarenhanced;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.awt.*;

public class AlarmActivation {
    private static final int BUFFER_SIZE = 4096;
    String title;
    String desc;

    public AlarmActivation(String title, String desc) throws AWTException {
        this.title = title;
        this.desc = desc;
    }

    public void displayTray() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image notification_image = Toolkit.getDefaultToolkit().createImage(System.getenv("APPDATA") + "\\Windows Calendar\\Assets\\Notification Image.png");

        TrayIcon trayIcon = new TrayIcon(notification_image, "Windows Calendar Notification");

        trayIcon.setImageAutoSize(true);

        trayIcon.setToolTip("");
        tray.add(trayIcon);

        trayIcon.displayMessage(this.title, this.desc, MessageType.INFO);
    }

    public void playDefaultNotiSound() 
    {
        
    }
}