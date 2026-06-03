package com.zachery.customcalendar.ObservationsHolidays;

import java.time.LocalDate;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.RELIGIOUS;

/**
 * Islamic holidays (Hijri calendar).
 *
 * The Islamic year is purely lunar: 12 months × ~29.53 days ≈ 354.367 days.
 * Each Gregorian year, Islamic dates shift ~10-11 days earlier.
 *
 * Key Hijri dates (offsets from 1 Muharram):
 *    1 Muharram       = Islamic New Year
 *   12 Rabi al-Awwal  = Mawlid al-Nabi   (+11 days)
 *    1 Ramadan        = Ramadan start    (+148 days approx)
 *    1 Shawwal        = Eid al-Fitr      (+178 days approx)
 *   10 Dhu al-Hijjah  = Eid al-Adha      (+317/318 days approx)
 *
 * Anchor: 1 Muharram 1422 AH = March 26, 2001 CE.
 */
final class IslamicHolidays
{
    private IslamicHolidays() {}

    static void addAll(List<Holiday> list, int year)
    {
        // There can be 1 or 2 Islamic years starting within a Gregorian year.
        // We compute the Hijri New Year that falls closest to Jan 1 of the given year
        // and then also the next one if it falls within the same Gregorian year.
        final LocalDate ANCHOR_HIJRI_NY = LocalDate.of(2001, 3, 26); // 1 Muharram 1422 AH
        final double    IY_DAYS         = 354.36707;                  // mean Islamic year

        // How many Islamic years since the anchor?
        long daysSinceAnchor = LocalDate.of(year, 1, 1).toEpochDay()
                             - ANCHOR_HIJRI_NY.toEpochDay();
        long islamicYearsSince = (long) Math.floor(daysSinceAnchor / IY_DAYS);

        // Compute the two candidate New Year dates (one might be late Dec of prior year)
        for (int offset = 0; offset <= 1; offset++)
        {
            long iy = islamicYearsSince + offset;
            LocalDate newYear = ANCHOR_HIJRI_NY.plusDays(Math.round(iy * IY_DAYS));

            // Only emit holidays whose New Year anchor falls in or just before this year
            if (newYear.getYear() > year) continue;

            addSet(list, newYear, year);
        }
    }

    private static void addSet(List<Holiday> list, LocalDate newYear, int targetYear)
    {
        addIfInYear(list, newYear,              "Islamic New Year",    "Islamic NY",  targetYear);
        addIfInYear(list, newYear.plusDays(11), "Mawlid al-Nabi",      "Mawlid",      targetYear);
        addIfInYear(list, newYear.plusDays(148),"Ramadan Begins",      "Ramadan",     targetYear);
        addIfInYear(list, newYear.plusDays(178),"Eid al-Fitr",         "Eid al-Fitr", targetYear);
        addIfInYear(list, newYear.plusDays(179),"Eid al-Fitr (day 2)", "Eid al-Fitr", targetYear);
        addIfInYear(list, newYear.plusDays(317),"Eid al-Adha",         "Eid al-Adha", targetYear);
        addIfInYear(list, newYear.plusDays(318),"Eid al-Adha (day 2)", "Eid al-Adha", targetYear);
    }

    /** Only adds the holiday if its date falls within the given target year. */
    private static void addIfInYear(List<Holiday> list, LocalDate date, String name,
                                    String shortName, int targetYear)
    {
        if (date.getYear() == targetYear)
            list.add(new Holiday(date, name, shortName, RELIGIOUS));
    }
}
