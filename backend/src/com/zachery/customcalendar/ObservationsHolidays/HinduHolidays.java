package com.zachery.customcalendar.ObservationsHolidays;

import java.time.LocalDate;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.shiftByYear;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.RELIGIOUS;

/**
 * Hindu / South Asian festivals.
 *
 * These use astronomical approximations anchored to known dates.
 *
 *  Diwali   - 15th day of Kartik (new moon of Kartik); falls Oct 13 - Nov 15
 *             Anchor: Diwali 2000 = Oct 26, 2000
 *  Holi     - last full moon of Phalguna; falls Feb 20 - Mar 22
 *             Anchor: Holi 2000 = Mar 20, 2000  (Holika Dahan eve)
 *  Dussehra - 10th day (Vijaya Dashami) of Navratri; falls Sep 22 - Oct 24
 *             Anchor: Dussehra 2000 = Oct 7, 2000
 *
 * The Hindu lunisolar year averages ~365.2587 days.
 */
final class HinduHolidays
{
    private HinduHolidays() {}

    static void addAll(List<Holiday> list, int year)
    {
        final double HINDU_YEAR = 365.25636; // sidereal year used by Hindu calendar

        // Diwali
        final LocalDate DIWALI_ANCHOR = LocalDate.of(2000, 10, 26);
        LocalDate diwali = shiftByYear(DIWALI_ANCHOR, year, 2000, HINDU_YEAR);
        // Diwali is always in Oct or Nov
        if (diwali.getMonthValue() < 9)  diwali = diwali.plusDays(Math.round(HINDU_YEAR));
        if (diwali.getMonthValue() > 11) diwali = diwali.minusDays(Math.round(HINDU_YEAR));
        list.add(new Holiday(diwali, "Diwali", "Diwali", RELIGIOUS));

        // Holi (Holika Dahan - the main bonfire night, eve of the color festival)
        final LocalDate HOLI_ANCHOR = LocalDate.of(2000, 3, 20);
        LocalDate holi = shiftByYear(HOLI_ANCHOR, year, 2000, HINDU_YEAR);
        if (holi.getMonthValue() < 2)  holi = holi.plusDays(Math.round(HINDU_YEAR));
        if (holi.getMonthValue() > 3)  holi = holi.minusDays(Math.round(HINDU_YEAR));
        list.add(new Holiday(holi,             "Holi (Holika Dahan)", "Holi", RELIGIOUS));
        list.add(new Holiday(holi.plusDays(1), "Holi",                "Holi", RELIGIOUS));

        // Dussehra / Vijaya Dashami
        final LocalDate DUSSEHRA_ANCHOR = LocalDate.of(2000, 10, 7);
        LocalDate dussehra = shiftByYear(DUSSEHRA_ANCHOR, year, 2000, HINDU_YEAR);
        if (dussehra.getMonthValue() < 9)  dussehra = dussehra.plusDays(Math.round(HINDU_YEAR));
        if (dussehra.getMonthValue() > 10) dussehra = dussehra.minusDays(Math.round(HINDU_YEAR));
        list.add(new Holiday(dussehra, "Dussehra (Vijaya Dashami)", "Dussehra", RELIGIOUS));
    }
}