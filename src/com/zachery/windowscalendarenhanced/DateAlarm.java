package com.zachery.windowscalendarenhanced;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.io.File;
import java.util.List;

/*
todo
Implement SQL Lite in app database for alarms instead of records by text file
*/

public class DateAlarm {
    Queue<LocalDateTime> alarms;

    //List of lists includes LocalDateTime(s) & notification information
    List<List<String>> alarms_data;

    //Creates a DataAlarm Object
    //Notification String data format: LocalDateTime | Title | Description (Key: | = New Line)
    public DateAlarm() throws IOException {
        this.alarms = new PriorityQueue<>();

        Scanner sc = getNotificationData();

        //Loads notification data into DataAlarm object
        while (sc.hasNextLine()) {
            String localDateTime = sc.nextLine();

            this.alarms.add(LocalDateTime.parse(localDateTime));
            List<String> data = new ArrayList<>();

            data.add(localDateTime);
            data.add(sc.nextLine());
            data.add(sc.nextLine());

            this.alarms_data.add(data);
        }


        long delay = Duration.between(LocalDateTime.now(), this.alarms.peek()).getSeconds();

        if (delay <= 0) {
            removeAlarm(this.alarms.peek());
        }
    }

    public void setAlarm(LocalDateTime time, String title, String desc) throws IOException {
        this.alarms.add(time);

        //Add alarm data to "Notification_Data" file
        PrintWriter pw = getPrintWriter();

        /*
        todo
        PrintWriter should add the future date time into the log and current Queue object.
        */

        pw.println(time);
        pw.println(title);
        pw.println(desc);
    }

    public void removeAlarm(LocalDateTime time) {
        this.alarms.remove(time);
        //todo
        //remove the alarm info from the Notification_Data.txt
    }

    //I learned somewhat about threads dealing with memory allocation via malloc! Can it come in handy here?
    //checkAlarm should be called when the application is started. From there it remains a background process.
    public void checkAlarm() throws InterruptedException {

        Thread thread = new Thread(() -> {
            long delay = Duration.between(this.alarms.peek(), LocalDateTime.now()).getSeconds();
            AlarmActivation alarm;

            try {
                if (delay > 0) {
                    Thread.sleep(delay * 1000);
                }

                for (List<String> data : this.alarms_data) {
                    if (Objects.equals(data.getFirst(), this.alarms.peek().toString())) {
                        alarm = new AlarmActivation(data.get(1), data.get(2));
                        alarm.displayTray();
                        alarm.playDefaultNotiSound();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
    }

    public Scanner getNotificationData() throws IOException  {
        Scanner sc;
        try {
            sc = new Scanner(new File(System.getenv("APPDATA") + "\\Windows Calendar\\Notification_Data"));
        } catch (FileNotFoundException e) {
            File Notification_Data = new File(System.getenv("APPDATA") + "\\Windows Calendar\\Notification_Data");
            boolean created = Notification_Data.createNewFile();
            if (created) {
                sc = new Scanner(new File(System.getenv("APPDATA") + "\\Windows Calendar\\Notification_Data"));
            } else {
                throw new FileNotFoundException(
                        "Failed to create Notification_Data file at: " + Notification_Data);
            }
        }
        return sc;
    }

    public PrintWriter getPrintWriter() throws IOException  {
        PrintWriter pw;

        try {
            pw = new PrintWriter(System.getenv("APPDATA") + "\\Windows Calendar\\Notification_Data");
        } catch (FileNotFoundException e) {
            File Notification_Data = new File(System.getenv("APPDATA") + "\\Windows Calendar\\Notification_Data");
            boolean created = Notification_Data.createNewFile();

            if (created) {
                pw = new PrintWriter(System.getenv("APPDATA") + "\\Windows Calendar\\Notification_Data");
            } else {
                throw new FileNotFoundException(
                        "Failed to create Notification_Data file at: " + Notification_Data);
            }
        }
        return pw;
    }
}

//Notification Example
//2026-18-03T17:05:00
//Homework
//Essentials of Software Engineering Chapter 6 Reading

//Notification Example
//2026-23-03T17:05:00
//Homework
//Essentials of Software Engineering Chapter 6 Reading