package com.zachery.customcalendar;

import java.time.LocalDateTime;

public record AlarmRecord(LocalDateTime time, String title, String desc) {}
