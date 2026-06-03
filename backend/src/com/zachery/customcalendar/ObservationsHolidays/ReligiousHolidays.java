package com.zachery.customcalendar.ObservationsHolidays;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeEaster;
import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeHanukkah;
import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.addSimple;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.RELIGIOUS;

/**
 * Religious observances. Handles the Christian / Easter-relative dates and
 * Hanukkah directly, then delegates to the per-faith providers for the
 * remaining Jewish, Islamic, and Hindu festivals.
 */
public final class ReligiousHolidays
{
    private ReligiousHolidays() {}

    public static void addAll(List<Holiday> list, int year)
    {
        LocalDate easter = computeEaster(year);

        // Easter-relative Christian dates
        list.add(new Holiday(easter.minusDays(46), "Ash Wednesday", "Ash Wed",   RELIGIOUS));
        list.add(new Holiday(easter.minusDays(7),  "Palm Sunday",   "Palm Sun",  RELIGIOUS));
        list.add(new Holiday(easter.minusDays(3),  "Holy Thursday", "Holy Thu",  RELIGIOUS));
        list.add(new Holiday(easter.minusDays(2),  "Good Friday",   "Good Fri",  RELIGIOUS));
        list.add(new Holiday(easter.minusDays(1),  "Holy Saturday", "Holy Sat",  RELIGIOUS));
        list.add(new Holiday(easter, "Easter Sunday", "Easter", RELIGIOUS));
        list.add(new Holiday(easter.plusDays(39), "Ascension Day", "Ascension", RELIGIOUS));
        list.add(new Holiday(easter.plusDays(49), "Pentecost", "Pentecost", RELIGIOUS));

        // Fixed-date Christian
        addSimple(list, year, Month.DECEMBER, 24, "Christmas Eve", "Xmas Eve", RELIGIOUS);
        addSimple(list, year, Month.DECEMBER, 25, "Christmas Day", "Christmas",RELIGIOUS);
        addSimple(list, year, Month.DECEMBER, 26, "Kwanzaa Begins", "Kwanzaa", RELIGIOUS);

        // Hanukkah - 25 Kislev (8 nights)
        LocalDate hanukkah = computeHanukkah(year);
        for (int i = 0; i < 8; i++)
        {
            LocalDate day   = hanukkah.plusDays(i);
            String    label = i == 0 ? "Hanukkah Begins" : (i == 7 ? "Hanukkah Ends" : "Hanukkah");
            list.add(new Holiday(day, label, "Hanukkah", RELIGIOUS));
        }

        // Per-faith festival sets
        JewishHolidays.addAll(list, year);
        IslamicHolidays.addAll(list, year);
        HinduHolidays.addAll(list, year);
    }
}
