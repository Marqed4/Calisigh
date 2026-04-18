package com.zachery.customcalendar;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Backgrounds
{
    private List<String> entries = new ArrayList<>();

    public Backgrounds()
    {
        try {
            loadBackgrounds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBackgrounds() throws IOException
    {
        java.io.File file = SystemDirectory.ObtainFile("Backgrounds/Backgrounds.txt");

        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
            return;
        }

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

    public void addBackground(String sourcePath) throws IOException
    {
        java.io.File sourceFile = new java.io.File(sourcePath);

        if (!sourceFile.exists())
            throw new IOException("Source file does not exist: " + sourcePath);

        String fileName  = sourceFile.getName();
        String displayName = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;

        java.io.File destFile = SystemDirectory.ObtainFile
                ("Backgrounds/Uploads/" + fileName);

        destFile.getParentFile().mkdirs();

        Files.copy(sourceFile.toPath(), destFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        // "Chocolate Forest|&C:/Users/.../Uploads/Chocolate Forest.gif"
        String entry = displayName + "|&" + destFile.getAbsolutePath().replace("\\", "/");

        if (entries.contains(entry))
            return;

        entries.add(entry);
        Collections.sort(entries);

        try (FileWriter fw = new FileWriter
                (SystemDirectory.ObtainFile("Backgrounds/Backgrounds.txt"), true);
             PrintWriter pw = new PrintWriter(fw))
        {
            pw.println(entry);
        }
    }

    // name = display name e.g. "Chocolate Forest"
    public void removeBackground(String name) throws IOException
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
            System.out.println("Background not found: " + name);
            return;
        }

        String filePath = entryToRemove.split("\\|&", 2)[1];
        java.io.File uploadedFile = new java.io.File(filePath);

        if (uploadedFile.exists())
            uploadedFile.delete();

        entries.remove(entryToRemove);
        rewriteBackgroundsFile();
    }

    private void rewriteBackgroundsFile() throws IOException
    {
        java.io.File file = SystemDirectory.ObtainFile("Backgrounds/Backgrounds.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false)))
        {
            for (String entry : entries)
                pw.println(entry);
        }
    }

    public List<String> getAllBackgrounds()
    {
        return Collections.unmodifiableList(entries);
    }

    public static String getDisplayName(String entry)
    {
        String[] parts = entry.split("\\|&", 2);
        return parts[0];
    }

    public static String getFilePath(String entry)
    {
        String[] parts = entry.split("\\|&", 2);
        return parts.length == 2 ? parts[1] : "";
    }
}