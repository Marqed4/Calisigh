package com.zachery.customcalendar;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Backgrounds {
    private static final int B_TREE_DEGREE = 3; // t=3: nodes hold 2–5 keys
    private BTree tree = new BTree(B_TREE_DEGREE);

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
            tree.insert(name + "|&" + path);
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
        if (tree.search(displayName) != null) return;

        File destFile = SystemDirectory.ObtainFile("Backgrounds/Uploads/" + fileName);
        destFile.getParentFile().mkdirs();
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        tree.insert(displayName + "|&" + destFile.getAbsolutePath().replace("\\", "/"));
        saveBackgrounds();
    }

    public void removeBackground(String name) throws IOException {
        String entry = tree.search(name);
        if (entry == null) {
            System.out.println("Background not found: " + name);
            return;
        }

        String filePath = Backgrounds.getFilePath(entry);
        File uploadedFile = new File(filePath);
        if (uploadedFile.exists()) uploadedFile.delete();

        tree.delete(name);
        saveBackgrounds();
    }

    private void saveBackgrounds() throws IOException {
        File file = SystemDirectory.ObtainFile("Backgrounds/Backgrounds.json");
        file.getParentFile().mkdirs();

        JsonArray arr = new JsonArray();
        for (String entry : tree.getAllEntries()) {
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
        return tree.getAllEntries();
    }

    public static String getDisplayName(String entry) {
        return entry.split("\\|&", 2)[0];
    }

    public static String getFilePath(String entry) {
        String[] parts = entry.split("\\|&", 2);
        return parts.length == 2 ? parts[1] : "";
    }
}