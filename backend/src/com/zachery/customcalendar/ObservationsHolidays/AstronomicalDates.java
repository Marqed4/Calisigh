package com.zachery.customcalendar.ObservationsHolidays;

import java.time.Month;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.addSimple;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.ASTRONOMICAL;

/**
 * Equinoxes and solstices.
 *
 * Approximate fixed dates accurate to ±1 day for the near future.
 * For precise times, use an astronomical library.
 */
public final class AstronomicalDates
{
    private AstronomicalDates() {}

    public static void addAll(List<Holiday> list, int year)
    {
        addSimple(list, year, Month.MARCH,     20, "Spring Equinox",   "Spr. Equinox", ASTRONOMICAL);
        addSimple(list, year, Month.JUNE,      21, "Summer Solstice",  "Sum. Solstice",ASTRONOMICAL);
        addSimple(list, year, Month.SEPTEMBER, 23, "Autumnal Equinox", "Fall Equinox", ASTRONOMICAL);
        addSimple(list, year, Month.DECEMBER,  21, "Winter Solstice",  "Win. Solstice",ASTRONOMICAL);
    }
}