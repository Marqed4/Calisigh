package com.zachery.customcalendar.ObservationsHolidays;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeEaster;
import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeLunarNewYear;
import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeMidAutumnFestival;
import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.addSimple;
import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.lastWeekday;
import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.nthWeekday;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.OBSERVANCE;

public final class Observances
{
    private Observances() {}

    public static void addAll(List<Holiday> list, int year)
    {
        // Fixed-date observances
        addSimple(list, year, Month.FEBRUARY,  2,  "Groundhog Day",           "Groundhog",    OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,  14, "Valentine's Day",         "Valentine's",  OBSERVANCE);
        addSimple(list, year, Month.MARCH,     17, "St. Patrick's Day",       "St. Pat's",    OBSERVANCE);
        addSimple(list, year, Month.APRIL,     22, "Earth Day",               "Earth Day",    OBSERVANCE);
        addSimple(list, year, Month.MAY,       5,  "Cinco de Mayo",           "Cinco Mayo",   OBSERVANCE);
        addSimple(list, year, Month.JUNE,      19, "Juneteenth (Obs.)",       "Juneteenth",   OBSERVANCE);
        addSimple(list, year, Month.JUNE,      26, "Trans Pride Day",         "Trans Pride",  OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   16, "Spirit Day",              "Spirit Day",   OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   31, "Halloween",               "Halloween",    OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,  1,  "Día de los Muertos",      "Día Muertos",  OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,  2,  "Día de los Muertos",      "Día Muertos",  OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  24, "Christmas Eve",           "Xmas Eve",     OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  31, "New Year's Eve",          "NYE",          OBSERVANCE);
        addSimple(list, year, Month.MARCH,     14, "Pi Day",                  "Pi Day",       OBSERVANCE);
        addSimple(list, year, Month.APRIL,     1,  "April Fools' Day",        "April Fools",  OBSERVANCE);
        addSimple(list, year, Month.APRIL,     15, "Tax Day",                 "Tax Day",      OBSERVANCE);
        addSimple(list, year, Month.APRIL,     20, "420",                     "420",          OBSERVANCE);
        addSimple(list, year, Month.MAY,       4,  "Star Wars Day",           "May the 4th",  OBSERVANCE);
        addSimple(list, year, Month.MAY,       19, "Malcolm X Day",           "Malcolm X",    OBSERVANCE);
        addSimple(list, year, Month.MAY,       22, "Harvey Milk Day",         "Harvey Milk",  OBSERVANCE);
        addSimple(list, year, Month.JUNE,      14, "Flag Day",                "Flag Day",     OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER, 11, "Patriot Day",             "Patriot Day",  OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER, 23, "Bisexual Visibility Day", "Bi Visibility",OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   11, "National Coming Out Day", "Coming Out",   OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   26, "Intersex Awareness Day",  "Intersex Day", OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,  20, "Transgender Day of Remembrance", "Trans Rememb.", OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  7,  "Pearl Harbor Remembrance Day", "Pearl Harbor", OBSERVANCE);

        // Dec 1 has two observances on the same date
        addSimple(list, year, Month.DECEMBER,  1,  "Rosa Parks Day",          "Rosa Parks",   OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  1,  "World AIDS Day",          "AIDS Day",     OBSERVANCE);

        // Boss's Day
        addSimple(list, year, Month.OCTOBER,   16, "Boss's Day",              "Boss's Day",   OBSERVANCE);

        // Heritage/Awareness month starts
        addSimple(list, year, Month.FEBRUARY,  1,  "Black History Month",     "BH Month",     OBSERVANCE);
        addSimple(list, year, Month.MARCH,     1,  "Women's History Month",   "WHM",          OBSERVANCE);
        addSimple(list, year, Month.JUNE,      1,  "Pride Month",             "Pride Month",  OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER, 15, "Hispanic Heritage Month", "Hisp. Heritage", OBSERVANCE);

        // Dynamic observances
        // Mother's Day - 2nd Sunday of May
        list.add(new Holiday(nthWeekday(year, Month.MAY,  DayOfWeek.SUNDAY, 2),
            "Mother's Day", "Mother's", OBSERVANCE));

        // Father's Day - 3rd Sunday of June
        list.add(new Holiday(nthWeekday(year, Month.JUNE, DayOfWeek.SUNDAY, 3),
            "Father's Day", "Father's", OBSERVANCE));

        // Mardi Gras - 47 days before Easter (Fat Tuesday)
        LocalDate easter = computeEaster(year);
        list.add(new Holiday(easter.minusDays(47),
            "Mardi Gras", "Mardi Gras", OBSERVANCE));

        // Administrative Professionals' Day - last Wednesday of April
        list.add(new Holiday(lastWeekday(year, Month.APRIL, DayOfWeek.WEDNESDAY),
            "Administrative Professionals' Day", "Admin Day", OBSERVANCE));

        // Grandparents' Day - 1st Sunday after Labor Day
        LocalDate laborDay = nthWeekday(year, Month.SEPTEMBER, DayOfWeek.MONDAY, 1);
        list.add(new Holiday(laborDay.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)),
            "Grandparents' Day", "Grandparents'", OBSERVANCE));

        // Daylight Saving Time start - 2nd Sunday in March
        list.add(new Holiday(nthWeekday(year, Month.MARCH, DayOfWeek.SUNDAY, 2),
            "Daylight Saving Time Begins", "DST Start", OBSERVANCE));

        // Daylight Saving Time end - 1st Sunday in November
        list.add(new Holiday(nthWeekday(year, Month.NOVEMBER, DayOfWeek.SUNDAY, 1),
            "Daylight Saving Time Ends", "DST End", OBSERVANCE));

        // Lunar New Year - 2nd new moon after Dec 21 solstice (1st new moon after Jan 21)
        list.add(new Holiday(computeLunarNewYear(year),
            "Lunar New Year", "Lunar NY", OBSERVANCE));

        // Lantern Festival - 15 days after Lunar New Year
        list.add(new Holiday(computeLunarNewYear(year).plusDays(15),
            "Lantern Festival", "Lantern Fest", OBSERVANCE));

        // Mid-Autumn Festival - 15th day of the 8th lunar month (~Sept/Oct full moon)
        list.add(new Holiday(computeMidAutumnFestival(year),
            "Mid-Autumn Festival", "Mid-Autumn", OBSERVANCE));

        // TODO:
        // Add more
    }
}
