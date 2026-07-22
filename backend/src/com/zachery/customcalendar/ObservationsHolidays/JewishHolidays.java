package com.zachery.customcalendar.ObservationsHolidays;

import java.time.LocalDate;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.Computus.computePassover;
import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeRoshHashanah;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.RELIGIOUS;

/**
 * Jewish high holidays and festivals (Hebrew calendar).
 *
 * All Tishri-based dates are offsets in days from 1 Tishri (Rosh Hashanah):
 *    1 Tishri  = Rosh Hashanah day 1
 *    2 Tishri  = Rosh Hashanah day 2
 *   10 Tishri  = Yom Kippur
 *   15 Tishri  = Sukkot begins  (7 days)
 *   22 Tishri  = Shemini Atzeret / Simchat Torah
 * Nisan 15     = Passover (Pesach) first night
 * Sivan 6      = Shavuot  - 50 days after the first day of Passover
 * Adar 14      = Purim    - falls ~30 days before Pesach
 */
final class JewishHolidays
{
    private JewishHolidays() {}

    static void addAll(List<Holiday> list, int year)
    {
        LocalDate roshHashanah = computeRoshHashanah(year);

        // Rosh Hashanah - 2 days
        list.add(new Holiday(roshHashanah, "Rosh Hashanah", "Rosh Hash.", RELIGIOUS));
        list.add(new Holiday(roshHashanah.plusDays(1), "Rosh Hashanah (day 2)", "Rosh Hash.", RELIGIOUS));

        // Yom Kippur - 10 Tishri
        list.add(new Holiday(roshHashanah.plusDays(9), "Yom Kippur", "Yom Kippur", RELIGIOUS));

        // Sukkot - 15 Tishri, 7 days
        LocalDate sukkot = roshHashanah.plusDays(14);
        for (int i = 0; i < 7; i++)
        {
            String label = i == 0 ? "Sukkot Begins" : (i == 6 ? "Sukkot Ends" : "Sukkot");
            list.add(new Holiday(sukkot.plusDays(i), label, "Sukkot", RELIGIOUS));
        }

        // Shemini Atzeret / Simchat Torah - 22 Tishri
        list.add(new Holiday(roshHashanah.plusDays(21), "Shemini Atzeret / Simchat Torah", "Simchat Torah", RELIGIOUS));

        // Purim - 14 Adar, approximately 30 days before Passover
        // Passover (Pesach) - 15 Nisan
        // Shavuot - 6 Sivan, exactly 50 days after the first day of Passover
        LocalDate passover = computePassover(year);
        list.add(new Holiday(passover.minusDays(30), "Purim", "Purim", RELIGIOUS));
        list.add(new Holiday(passover, "Passover (Pesach)", "Passover", RELIGIOUS));
        list.add(new Holiday(passover.plusDays(6), "Passover ends", "Passover", RELIGIOUS));
        list.add(new Holiday(passover.plusDays(50), "Shavuot", "Shavuot", RELIGIOUS));
    }
}
