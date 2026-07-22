package com.zachery.customcalendar;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Backgrounds {
    private final List<String> entries = new ArrayList<>();

    public Backgrounds() {
        try {
            loadBackgrounds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBackgrounds() throws IOException {
        File file = SystemDirectory.ObtainFile("Backgrounds/Backgrounds.json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return;
        }

        String json = new String(Files.readAllBytes(file.toPath()));
        JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String name = obj.get("name").getAsString();
            String path = obj.get("path").getAsString();
            entries.add(name + "|&" + path);
        }
    }

    public void addBackground(String sourcePath) throws IOException {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists())
            throw new IOException("Source file does not exist: " + sourcePath);

        String fileName = sourceFile.getName();
        String displayName = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;

        // Skip if already exists
        if (findEntry(displayName) != null) return;

        File destFile = SystemDirectory.ObtainFile("Backgrounds/Uploads/" + fileName);
        destFile.getParentFile().mkdirs();
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        entries.add(displayName + "|&" + destFile.getAbsolutePath().replace("\\", "/"));
        saveBackgrounds();
    }

    public void removeBackground(String name) throws IOException {
        String entry = findEntry(name);
        if (entry == null) {
            System.out.println("Background not found: " + name);
            return;
        }

        String filePath = Backgrounds.getFilePath(entry);
        File uploadedFile = new File(filePath);
        if (uploadedFile.exists()) uploadedFile.delete();

        entries.remove(entry);
        saveBackgrounds();
    }

    private String findEntry(String name) {
        for (String entry : entries) {
            if (getDisplayName(entry).equals(name)) return entry;
        }
        return null;
    }

    private void saveBackgrounds() throws IOException {
        File file = SystemDirectory.ObtainFile("Backgrounds/Backgrounds.json");
        file.getParentFile().mkdirs();

        JsonArray arr = new JsonArray();
        for (String entry : entries) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", getDisplayName(entry));
            obj.addProperty("path", getFilePath(entry));
            arr.add(obj);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print(gson.toJson(arr));
        }
    }

    public List<String> getAllBackgrounds() {
        return entries;
    }

    public static String getDisplayName(String entry) {
        return entry.split("\\|&", 2)[0];
    }

    public static String getFilePath(String entry) {
        String[] parts = entry.split("\\|&", 2);
        return parts.length == 2 ? parts[1] : "";
    }
}
