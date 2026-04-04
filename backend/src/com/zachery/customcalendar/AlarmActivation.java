package com.zachery.customcalendar;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AlarmActivation
{
    String title;
    String desc;

    public AlarmActivation(String title, String desc)
    {
        this.title = title;
        this.desc = desc;
    }

    public AlarmActivation displayTray() throws Exception
    {
        System.out.println("Displaying Windows 11 toast notification...");

        String safeTitle = title.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        String safeDesc = desc.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

        // Write PowerShell script to a temp file to avoid quoting issues
        File psFile = File.createTempFile("toast_", ".ps1");
        psFile.deleteOnExit();

        try (PrintWriter pw = new PrintWriter(new FileWriter(psFile)))
        {
            pw.println("[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType=WindowsRuntime] | Out-Null");
            pw.println("[Windows.Data.Xml.Dom.XmlDocument, Windows.Data.Xml.Dom.XmlDocument, ContentType=WindowsRuntime] | Out-Null");
            pw.println("$xml = '<toast><visual><binding template=\"ToastGeneric\"><text>" + safeTitle + "</text><text>" + safeDesc + "</text></binding></visual></toast>'");
            pw.println("$doc = [Windows.Data.Xml.Dom.XmlDocument]::new()");
            pw.println("$doc.LoadXml($xml)");
            pw.println("$toast = [Windows.UI.Notifications.ToastNotification]::new($doc)");
            pw.println("[Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('Custom Calendar').Show($toast)");
        }

        ProcessBuilder pb = new ProcessBuilder("powershell", "-ExecutionPolicy", "Bypass", "-File", psFile.getAbsolutePath());
        pb.inheritIO();
        pb.start().waitFor();

        System.out.println("Toast notification sent!");

        return this;
    }

    public AlarmActivation playSound()
    {
        try
        {
            File sound = SystemDirectory.Directory("resources/assets/sounds/windows-10 calendar chime.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(sound);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
        catch (Exception e)
        {
            System.err.println("Missing Notification Audio: " + e.getMessage());
        }

        return this;
    }
}