package com.zachery.customcalendar;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDateTime;

/*
todo
Priority:

2. Up & Down buttons to switch/navigate between weeks.
3. Allow the right clicking on the pane shows event menu.
4. Ability to view calendar by Day/ Week/ Year.

Luxury:
1. Bottom left of screen allows an email to be sent.
2. Seven day forecast, showing weather conditions of the following seven days.
*/

public class Main
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting...");
        System.out.flush();

        Gson gson = new Gson();
        DateAlarm dateAlarm = new DateAlarm();

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, DELETE");
            res.header("Access-Control-Allow-Headers", "Content-Type");
        });

        options("/*", (req, res) -> "OK");

        get("/api/alarms", (req, res) -> {
            res.type("application/json");
            return gson.toJson(dateAlarm.alarmDataQueue);
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
                dateAlarm.checkAlarm();
                return gson.toJson(new MessageResponse("Alarm set!"));
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new MessageResponse("Failed: " + e.getMessage()));
            }
        });

        dateAlarm.checkAlarm();
        System.out.println("Java backend running on http://localhost:4567");
        System.out.flush();
    }

    static class AlarmRequest
    {
        String time, title, desc;
    }

    static class MessageResponse
    {
        String message;
        MessageResponse(String msg) { this.message = msg; }
    }
}