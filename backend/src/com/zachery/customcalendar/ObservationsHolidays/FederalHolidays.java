package com.zachery.customcalendar.ObservationsHolidays;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.addFixed;
import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.lastWeekday;
import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.nthWeekday;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.FEDERAL;

public final class FederalHolidays
{
    private FederalHolidays() {}

    public static void addAll(List<Holiday> list, int year)
    {
        // Fixed-date
        addFixed(list, year, Month.JANUARY,  1,  "New Year's Day",   "New Year's", FEDERAL);
        addFixed(list, year, Month.JUNE,     19, "Juneteenth",       "Juneteenth", FEDERAL);
        addFixed(list, year, Month.JULY,     4,  "Independence Day", "Indep. Day", FEDERAL);
        addFixed(list, year, Month.NOVEMBER, 11, "Veterans Day",     "Veterans",   FEDERAL);
        addFixed(list, year, Month.DECEMBER, 25, "Christmas Day",    "Christmas",  FEDERAL);

        // Floating
        list.add(new Holiday(nthWeekday(year, Month.JANUARY, DayOfWeek.MONDAY, 3),
            "Martin Luther King Jr. Day", "MLK Day", FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.FEBRUARY, DayOfWeek.MONDAY, 3),
            "Presidents' Day","Pres. Day", FEDERAL));
        list.add(new Holiday(lastWeekday(year, Month.MAY, DayOfWeek.MONDAY),
            "Memorial Day", "Memorial", FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.SEPTEMBER, DayOfWeek.MONDAY, 1),
            "Labor Day", "Labor Day", FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.OCTOBER, DayOfWeek.MONDAY, 2),
            "Columbus Day", "Columbus", FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.NOVEMBER, DayOfWeek.THURSDAY, 4),
            "Thanksgiving", "Thanksgiving", FEDERAL));

        // Election Day - 1st Tuesday after the 1st Monday in November
        LocalDate firstMondayNov = nthWeekday(year, Month.NOVEMBER, DayOfWeek.MONDAY, 1);
        list.add(new Holiday(firstMondayNov.plusDays(1),
            "Election Day", "Election", FEDERAL));

        // Armed Forces Day - 3rd Saturday in May
        list.add(new Holiday(nthWeekday(year, Month.MAY, DayOfWeek.SATURDAY, 3),
            "Armed Forces Day", "Armed Forces", FEDERAL));

        // Indigenous Peoples' Day - same floating date as Columbus Day (2nd Monday in October)
        list.add(new Holiday(nthWeekday(year, Month.OCTOBER, DayOfWeek.MONDAY, 2),
            "Indigenous Peoples' Day", "Indigenous", FEDERAL));
    }
}
