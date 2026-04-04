package com.zachery.customcalendar;

import java.time.LocalDateTime;

public record AlarmRecord(LocalDateTime time, String title, String desc) implements Comparable<AlarmRecord>
{
    @Override
    public int compareTo(AlarmRecord other)
    {
        return this.time.compareTo(other.time);
    }
}