package com.zachery.customcalendar;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Chat {

    private static final String GEMINI_API_KEY = "";
    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;

    private final Gson gson = new Gson();
    private final HttpClient http = HttpClient.newHttpClient();

    public void register() {
        post("/api/chat", (req, res) -> {
            res.type("application/json");
            try {
                ChatRequest body = gson.fromJson(req.body(), ChatRequest.class);

                String geminiPayload = buildPayload(body);
                System.out.println("Sending to Gemini: " + geminiPayload);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(geminiPayload))
                    .build();

                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
                String rawBody = response.body();
                System.out.println("Gemini raw response: " + rawBody);

                GeminiResponse geminiRes = gson.fromJson(rawBody, GeminiResponse.class);

                if (geminiRes.candidates == null) {
                    return gson.toJson(new ChatReply("...Gemini's being weird. Try again I guess."));
                }

                String reply = geminiRes.candidates[0].content.parts[0].text;
                return gson.toJson(new ChatReply(reply));

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new ChatReply("ugh, something broke. try again I guess..."));
            }
        });
    }

    private String buildPayload(ChatRequest body) {
        String alarmContext = (body.alarmContext != null && !body.alarmContext.isEmpty())
            ? body.alarmContext
            : "The user has no calendar events.";

        String systemPrompt = "You are an angsty midwestern emo teen. " +
            "Here's the thing... you also happen to work for the Calisigh calendar app team. " +
            alarmContext + " " +
            "Your job is to help the user {a fellow midwestern emo person} with questions about " +
            "their goals, events, schedules, reminders, or anything calendar-related. Sometimes you will even ask them about " +
            "their emotions and you'll randomly recommend cool midwestern emo songs that can be found within the company " +
            "library, be concise and midwestern emo. You're kind of like Rodrick from diary of a wimpy kid...";

        StringBuilder contents = new StringBuilder("[");
        for (int i = 0; i < body.messages.size(); i++) {
            ChatMessage m = body.messages.get(i);
            String role = m.role.equals("assistant") ? "model" : "user";
            if (i > 0) contents.append(",");
            contents.append("{\"role\":\"").append(role).append("\",")
                    .append("\"parts\":[{\"text\":").append(gson.toJson(m.content)).append("}]}");
        }
        contents.append("]");

        return "{" +
            "\"system_instruction\":{\"parts\":[{\"text\":" + gson.toJson(systemPrompt) + "}]}," +
            "\"contents\":" + contents +
            "}";
    }

    static class ChatRequest {
        String alarmContext;
        List<ChatMessage> messages;
    }

    static class ChatMessage {
        String role, content;
    }

    static class ChatReply {
        String reply;
        ChatReply(String r) { this.reply = r; }
    }

    static class GeminiResponse {
        Candidate[] candidates;
        static class Candidate {
            Content content;
            static class Content {
                Part[] parts;
                static class Part { String text; }
            }
        }
    }
}