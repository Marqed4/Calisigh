package com.zachery.customcalendar.ObservationsHolidays;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Generic calendar plumbing reused by every holiday provider. None of these
 * methods know anything about a specific holiday.
 *
 * Visibility is package-private (no modifier on the methods): every provider
 * lives in com.zachery.customcalendar, so package access is sufficient.
 */
final class DateUtil
{
    private DateUtil() {}

    /**
     * Fixed-date holiday that also emits an "(Observed)" entry when the date
     * lands on a weekend. Used for federal holidays.
     */
    static void addFixed(List<Holiday> list, int year, Month month, int day,
                         String name, String shortName, String category)
    {
        LocalDate actual = LocalDate.of(year, month, day);
        list.add(new Holiday(actual, name, shortName, category));

        DayOfWeek dow = actual.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY)
            list.add(new Holiday(actual.minusDays(1), name + 
        " (Observed)", shortName + "*", category));
        else if (dow == DayOfWeek.SUNDAY)
            list.add(new Holiday(actual.plusDays(1), name + 
        " (Observed)", shortName + "*", category));
    }

    // Simple/Simplified fixed-date entry with no observed-date shift.
    static void addSimple(List<Holiday> list, int year, Month month, int day,
                          String name, String shortName, String category)
    {
        list.add(new Holiday(LocalDate.of(year, month, day), name, shortName, category));
    }

    /** The nth occurrence of {@code dow} in {@code month} (n is 1-based). */
    static LocalDate nthWeekday(int year, Month month, DayOfWeek dow, int n)
    {
        return LocalDate.of(year, month, 1)
            .with(TemporalAdjusters.nextOrSame(dow))
            .plusWeeks(n - 1);
    }

    /** The last occurrence of {@code dow} in {@code month}. */
    static LocalDate lastWeekday(int year, Month month, DayOfWeek dow)
    {
        return LocalDate.of(year, month, 1)
            .with(TemporalAdjusters.lastDayOfMonth())
            .with(TemporalAdjusters.previousOrSame(dow));
    }

    /** Shifts an anchor date forward by an integer number of "year" lengths. */
    static LocalDate shiftByYear(LocalDate anchor, int year, int anchorYear, double yearDays)
    {
        long diff = Math.round((year - anchorYear) * yearDays);
        return anchor.plusDays(diff);
    }
}