package com.zachery.windowscalendarenhanced;

import javafx.scene.media.MediaPlayer;

import java.awt.TrayIcon.MessageType;

import javafx.scene.media.Media;

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

    public void playDefaultNotiSound() {
        File noti_sound = new File(System.getenv("APPDATA") + "\\Windows Calendar\\Assets\\Windows Notification.mp3");
        MediaPlayer player1 = new MediaPlayer(new Media(noti_sound.toURI().toString()));
        player1.play();

        File chime_sound  = new File(System.getenv("APPDATA") + "\\Windows Calendar\\Assets\\Windows 10 Calendar Chime.mp3\"");
        MediaPlayer player2 = new MediaPlayer(new Media(chime_sound.toURI().toString()));
        player2.play();
    }
}