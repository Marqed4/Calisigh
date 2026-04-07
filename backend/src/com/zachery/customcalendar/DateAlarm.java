package com.zachery.customcalendar;

import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.List;

public class DateAlarm
{
    //List of lists includes LocalDateTime(s) & notification information
    PriorityQueue<AlarmRecord> alarmDataQueue = new PriorityQueue<>();
    List<AlarmRecord> alarmDataList = new ArrayList<>();

    private Thread alarmThread = null;

    //Notification String data format: LocalDateTime|&^Title|&^Description (Key: |&^ = Seperation)
    public DateAlarm() throws IOException 
    {
        Scanner scan = getAllNotificationData();

        while (scan.hasNextLine()) 
        {
            String line = scan.nextLine();
            String[] parts = line.split("\\|&\\^");

            String title = parts[1].replace("<NL>", "\n");
            String desc  = (parts.length > 2 ? parts[2] : "").replace("<aNL>", "\n");

            AlarmRecord alarm = new AlarmRecord(
                LocalDateTime.parse(parts[0]),
                title,
                desc
            );

            alarmDataQueue.add(alarm);
        }

        // Sync display list BEFORE purge so old alarms still show on calendar
        alarmDataList = new ArrayList<>(alarmDataQueue);

        // Remove expired alarms from the firing queue only
        while (!alarmDataQueue.isEmpty()) 
        {
            AlarmRecord next = alarmDataQueue.peek();
            long delay = Duration.between(LocalDateTime.now(), next.time()).getSeconds();

            if (delay <= 0) {
                alarmDataQueue.poll();
            } else {
                break;
            }
        }
    }


    public void setAlarm(LocalDateTime time, String title, String desc) throws IOException 
    {
        String safeTitle = title.replace("\n", "<NL>");
        String safeDesc = desc.replace("\n", "<aNL>");

        AlarmRecord alarm = new AlarmRecord(time, title, desc);
        alarmDataQueue.add(alarm);
        alarmDataList.add(alarm);

        // Add alarm data to notifications.txt in CustomCalendar appdata's directory
        try (FileWriter fw = new FileWriter(SystemDirectory.ObtainFile("notifications/notifications.txt"), true);
        PrintWriter pw = new PrintWriter(fw)) 
        {
            pw.print(time + "|&^");
            pw.print(safeTitle + "|&^");
            pw.print(safeDesc + "\n");
        }

        // Restart the alarm thread to pick up the newly added alarm
        checkAlarm();
    }

    public void removeAlarm(LocalDateTime time)
    {
        alarmDataQueue.removeIf(now -> now.time().equals(time));
        alarmDataList.removeIf(now -> now.time().equals(time));
        // todo: remove from notifications.txt
    }

    // checkAlarm starts or restarts the alarm thread.
    // If a thread is already sleeping, it is interrupted so it re-evaluates the queue.
    public void checkAlarm()
    {
        // Interrupt existing thread so it wakes up and re-checks the queue
        if (alarmThread != null && alarmThread.isAlive())
        {
            alarmThread.interrupt();
        }

        alarmThread = new Thread(() -> 
        {
            while (!alarmDataQueue.isEmpty()) 
            {
                AlarmRecord nextAlarm = alarmDataQueue.peek();
                if (nextAlarm == null) break;

                long delay = Duration.between(LocalDateTime.now(), nextAlarm.time()).getSeconds();

                try {
                    if (delay > 0) 
                    {
                        Thread.sleep(delay * 1000);
                    }

                    // Fire the CANNONS
                    AlarmRecord CANNON = alarmDataQueue.poll();
                    new AlarmActivation
                    (
                        CANNON.title(),
                        CANNON.desc()
                    )
                    .displayTray()
                    .playSound();
                } 
                catch (InterruptedException e) 
                {
                    // A new alarm was added — clear the flag and re-check the queue from the top
                    Thread.interrupted();
                    continue;
                } 
                catch (Exception e) 
                {
                    System.err.println("Error firing alarm: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        alarmThread.setDaemon(true);
        alarmThread.start();
    }


    // Helper Functions
    private Scanner getAllNotificationData() throws IOException 
    {
        java.io.File file = SystemDirectory.ObtainFile("notifications/notifications.txt");
        if (!file.exists()) 
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return new Scanner(file);
    }
}

//Notification Examples (Ignore "No. ")
//1. 2026-20-02T17:05:00|&^Homework|&^Essentials of Software Engineering Chapter 6 Reading
//2. 2026-24-03T17:05:00|&^Homework|&^Essentials of Software Engineering Chapter 12 Reading
//3. 2026-26-03T17:05:00|&^Homework|&^CISC. 3810/7510: Database Systems: Database Design
//4. 2026-22-03T17:13:35|&^Dave's Donuts|&^- 2 Vanilla w/ Fresh Strawberry Jam $3<aNL>- 1 Chocolate $3