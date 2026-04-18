package com.zachery.customcalendar;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javazoom.jl.player.advanced.AdvancedPlayer;

public class AlarmSounds
{
    private static final String SELECTED_SOUND_FILE = "AlarmSounds/SelectedSound.txt";

    private List<String> entries = new ArrayList<>();
    private Thread currentPlayback;
    private Clip currentClip;
    private AdvancedPlayer currentMp3Player;
    private String selectedSound = null;

    public AlarmSounds()
    {
        try {
            loadSounds();
            loadSelectedSound();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSounds() throws IOException
    {
        java.io.File file = SystemDirectory.Directory("AlarmSounds/AlarmSounds.txt");

        if (!file.exists() || file.length() == 0)
            return;

        entries.clear();

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty())
                    entries.add(line);
            }
        }

        Collections.sort(entries);
    }

    private void loadSelectedSound() throws IOException
    {
        java.io.File file = SystemDirectory.Directory(SELECTED_SOUND_FILE);

        if (!file.exists() || file.length() == 0) return;

        try (Scanner scanner = new Scanner(file))
        {
            if (scanner.hasNextLine())
            {
                String line = scanner.nextLine().trim();
                selectedSound = line.isEmpty() ? null : line;
            }
        }
    }

    public void addSound(String sourcePath) throws IOException
    {
        java.io.File sourceFile = new java.io.File(sourcePath);

        if (!sourceFile.exists())
            throw new IOException("Source file does not exist: " + sourcePath);

        String fileName = sourceFile.getName();
        String ext = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();

        if (!ext.equals(".mp3") && !ext.equals(".wav"))
            throw new IOException("Unsupported file type: " + ext);

        String displayName = fileName.substring(0, fileName.lastIndexOf('.'));

        java.io.File destFile = SystemDirectory.ObtainFile("AlarmSounds/Uploads/" + fileName);
        destFile.getParentFile().mkdirs();

        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        String entry = displayName + "|&" + destFile.getAbsolutePath();

        if (entries.contains(entry))
            return;

        entries.add(entry);
        Collections.sort(entries);

        try (FileWriter fw = new FileWriter(SystemDirectory.ObtainFile("AlarmSounds/AlarmSounds.txt"), true);
             PrintWriter pw = new PrintWriter(fw))
        {
            pw.println(entry);
        }
    }

    public void playSound(String name)
    {
        if (name == null || name.equals("chime") || name.isEmpty())
        {
            playSoundFile(null);
            return;
        }

        String filePath = null;

        for (String entry : entries)
        {
            String[] parts = entry.split("\\|&", 2);
            if (parts.length == 2 && parts[0].equals(name))
            {
                filePath = parts[1];
                break;
            }
        }

        if (filePath == null)
        {
            System.out.println("Sound not found: " + name);
            return;
        }

        playSoundFile(filePath);
    }

    private void playSoundFile(String filePath)
    {
        stopSound();

        try
        {
            java.io.File soundFile = (filePath != null && !filePath.isEmpty())
                ? new java.io.File(filePath)
                : SystemDirectory.Directory("resources/assets/sounds/Default Chime.mp3");

            if (!soundFile.exists())
            {
                System.err.println("Sound file not found: " + soundFile.getAbsolutePath());
                return;
            }

            String name = soundFile.getName().toLowerCase();

            if (name.endsWith(".mp3"))
            {
                java.io.File ref = soundFile;
                currentPlayback = new Thread(() ->
                {
                    try (FileInputStream fis = new FileInputStream(ref))
                    {
                        synchronized (this) {
                            currentMp3Player = new AdvancedPlayer(fis);
                        }
                        currentMp3Player.play();
                    }
                    catch (javazoom.jl.decoder.JavaLayerException e)
                    {
                        if (!Thread.currentThread().isInterrupted())
                            System.err.println("MP3 playback error: " + e.getMessage());
                    }
                    catch (Exception e)
                    {
                        if (!Thread.currentThread().isInterrupted())
                            System.err.println("MP3 playback error: " + e.getMessage());
                    }
                });
            }
            else if (name.endsWith(".wav"))
            {
                java.io.File ref = soundFile;
                currentPlayback = new Thread(() ->
                {
                    try (AudioInputStream ais = AudioSystem.getAudioInputStream(ref))
                    {
                        synchronized (this) {
                            currentClip = AudioSystem.getClip();
                        }
                        currentClip.open(ais);
                        currentClip.start();

                        long durationMs = currentClip.getMicrosecondLength() / 1000;
                        Thread.sleep(durationMs);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                    catch (Exception e)
                    {
                        if (!Thread.currentThread().isInterrupted())
                            System.err.println("WAV playback error: " + e.getMessage());
                    }
                    finally
                    {
                        synchronized (this) {
                            if (currentClip != null) {
                                currentClip.stop();
                                currentClip.close();
                                currentClip = null;
                            }
                        }
                    }
                });
            }
            else
            {
                System.err.println("Unsupported format: " + name);
                return;
            }

            currentPlayback.setDaemon(true);
            currentPlayback.start();
        }
        catch (Exception e)
        {
            System.err.println("Failed to play sound: " + e.getMessage());
        }
    }

    public void stopSound()
    {
        synchronized (this) {
            if (currentMp3Player != null) {
                currentMp3Player.close();
                currentMp3Player = null;
            }

            if (currentClip != null) {
                currentClip.stop();
                currentClip.close();
                currentClip = null;
            }
        }
        if (currentPlayback != null && currentPlayback.isAlive())
        {
            currentPlayback.interrupt();
            currentPlayback = null;
        }
    }

    public void removeSound(String name) throws IOException
    {
        String entryToRemove = null;

        for (String entry : entries)
        {
            String[] parts = entry.split("\\|&", 2);
            if (parts.length == 2 && parts[0].equals(name))
            {
                entryToRemove = entry;
                break;
            }
        }

        if (entryToRemove == null)
        {
            System.out.println("Sound not found: " + name);
            return;
        }

        String filePath = entryToRemove.split("\\|&", 2)[1];
        java.io.File uploadedFile = new java.io.File(filePath);

        if (uploadedFile.exists())
            uploadedFile.delete();

        entries.remove(entryToRemove);
        rewriteSoundsFile();
    }

    private void rewriteSoundsFile() throws IOException
    {
        java.io.File file = SystemDirectory.ObtainFile("AlarmSounds/AlarmSounds.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false)))
        {
            for (String entry : entries)
                pw.println(entry);
        }
    }

    public List<String> getAllSounds()
    {
        return Collections.unmodifiableList(entries);
    }

    public static String getDisplayName(String entry)
    {
        return entry.split("\\|&", 2)[0];
    }

    public static String getFilePath(String entry)
    {
        String[] parts = entry.split("\\|&", 2);
        return parts.length == 2 ? parts[1] : "";
    }

    public void selectSound(String name) throws IOException
    {
        selectedSound = (name == null || name.isEmpty()) ? null : name;
        System.out.println("=== selectSound called with: '" + name + "' -> stored as: '" + selectedSound + "'");

        java.io.File file = SystemDirectory.ObtainFile(SELECTED_SOUND_FILE);
        System.out.println("=== Writing to: " + file.getAbsolutePath());
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false)))
        {
            pw.println(selectedSound == null ? "" : selectedSound);
        }
        System.out.println("=== File size after write: " + file.length() + " bytes");
    }

    public String getSelectedSound()
    {
        return selectedSound;
    }
}