package com.zachery.customcalendar;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        SystemDirectory.seedDefaultSounds();

        System.out.println("Starting...");
        System.out.flush();

        port(4567);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                (com.google.gson.JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                    new com.google.gson.JsonPrimitive(src.toString()))
            .create();

        AlarmSounds alarmSounds;
        try {
            alarmSounds = new AlarmSounds();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        DateAlarm dateAlarm;
        try {
            dateAlarm = new DateAlarm(alarmSounds);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        Backgrounds backgrounds;
        try {
            backgrounds = new Backgrounds();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            res.header("Access-Control-Allow-Headers", "Content-Type");
        });

        options("/*", (req, res) -> {
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            return "OK";
        });

        get("/api/alarms", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(dateAlarm.alarmDataList);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });

        post("/api/alarms", (req, res) -> {
            res.type("application/json");
            try {
                AlarmRequest body = gson.fromJson(req.body(), AlarmRequest.class);
                dateAlarm.setAlarm(
                    LocalDateTime.parse(body.time),
                    body.title,
                    body.desc
                );
                return gson.toJson(new MessageResponse("Alarm set!"));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        delete("/api/alarms", (req, res) -> {
            res.type("application/json");
            try {
                AlarmRequest body = gson.fromJson(req.body(), AlarmRequest.class);
                dateAlarm.removeAlarm(java.time.LocalDateTime.parse(body.time));
                return gson.toJson(new MessageResponse("Alarm removed!"));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        put("/api/alarms/:id", (req, res) -> {
            res.type("application/json");
            try {
                String id = req.params(":id");
                AlarmRequest body = gson.fromJson(req.body(), AlarmRequest.class);
                dateAlarm.updateAlarm(
                    id,
                    LocalDateTime.parse(body.time),
                    body.title,
                    body.desc
                );
                return gson.toJson(new MessageResponse("Alarm updated..."));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        get("/api/backgrounds", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(backgrounds.getAllBackgrounds());
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });

        post("/api/backgrounds", (req, res) -> {
            res.type("application/json");
            try {
                BackgroundRequest body = gson.fromJson(req.body(), BackgroundRequest.class);
                backgrounds.addBackground(body.sourcePath);
                return gson.toJson(new MessageResponse("Background added!"));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        delete("/api/backgrounds", (req, res) -> {
            res.type("application/json");
            try {
                BackgroundRequest body = gson.fromJson(req.body(), BackgroundRequest.class);
                backgrounds.removeBackground(body.name);
                return gson.toJson(new MessageResponse("Background removed!"));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        get("/api/sounds", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(alarmSounds.getAllSounds());
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });

        get("/api/sounds/selected", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(alarmSounds.getSelectedSound());
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "null";
            }
        });

        post("/api/sounds/play", (req, res) -> {
            res.type("application/json");
            try {
                SoundRequest body = gson.fromJson(req.body(), SoundRequest.class);
                alarmSounds.playSound(body.name);
                return gson.toJson(new MessageResponse("Playing: " + body.name));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        post("/api/sounds/stop", (req, res) -> {
            res.type("application/json");
            try {
                alarmSounds.stopSound();
                return gson.toJson(new MessageResponse("Sound stopped."));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        post("/api/sounds/select", (req, res) -> {
            res.type("application/json");
            try {
                SoundRequest body = gson.fromJson(req.body(), SoundRequest.class);
                alarmSounds.selectSound(body.name);
                return gson.toJson(new MessageResponse("Sound selected: " + body.name));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        post("/api/sounds", (req, res) -> {
            res.type("application/json");
            try {
                SoundRequest body = gson.fromJson(req.body(), SoundRequest.class);
                alarmSounds.addSound(body.sourcePath);
                return gson.toJson(new MessageResponse("Sound added!"));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        delete("/api/sounds", (req, res) -> {
            res.type("application/json");
            try {
                SoundRequest body = gson.fromJson(req.body(), SoundRequest.class);
                alarmSounds.removeSound(body.name);
                return gson.toJson(new MessageResponse("Sound removed!"));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        dateAlarm.checkAlarm();
        new Chat().register();

        System.out.println("Java backend running on http://localhost:4567");
        System.out.flush();
    }

    static class AlarmRequest
    {
        String time, title, desc;
    }

    static class BackgroundRequest
    {
        String sourcePath, name;
    }

    static class SoundRequest
    {
        String sourcePath, name;
    }

    static class MessageResponse
    {
        String message;
        MessageResponse(String msg) { this.message = msg; }
    }
}