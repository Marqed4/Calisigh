package com.zachery.customcalendar.ObservationsHolidays;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeEaster;
import static com.zachery.customcalendar.ObservationsHolidays.Computus.computeOrthodoxEaster;
import static com.zachery.customcalendar.ObservationsHolidays.DateUtil.addSimple;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.OBSERVANCE;
import static com.zachery.customcalendar.ObservationsHolidays.HolidayCategory.RELIGIOUS;

/**
 * Widely observed public holidays across Poland, Ukraine, Czech Republic,
 * Slovakia, Hungary, Romania, Bulgaria, Croatia, Serbia, and the Baltic states.
 */
public final class EasternEuropeanHolidays
{
    private EasternEuropeanHolidays() {}

    public static void addAll(List<Holiday> list, int year)
    {
        // ---- Fixed civil holidays common across the region ----
        addSimple(list, year, Month.JANUARY,   1,  "New Year's Day (EE)",          "NY (EE)",       OBSERVANCE);
        addSimple(list, year, Month.MAY,        1,  "International Workers' Day",   "Workers' Day",  OBSERVANCE);
        addSimple(list, year, Month.MAY,        8,  "Victory in Europe Day (EE)",   "VE Day (EE)",   OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   11, "Independence Day (Poland)",    "PL Independence",OBSERVANCE);

        // ---- Specific national days ----
        addSimple(list, year, Month.MARCH,      15, "Hungarian National Day",       "HU Natl Day",   OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     20, "St. Stephen's Day (Hungary)",  "HU St. Stephen",OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,    23, "Hungarian Republic Day",       "HU Republic",   OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER,  28, "Czech Statehood Day",          "CZ Statehood",  OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,    28, "Czech Independence Day",       "CZ Independence",OBSERVANCE);
        addSimple(list, year, Month.JANUARY,    17, "Slovak Statehood Day",         "SK Statehood",  OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     29, "Slovak National Uprising Day", "SK Uprising",   OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     24, "Ukraine Independence Day",     "UA Independence",OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,    14, "Defender of Ukraine Day",      "UA Defender",   OBSERVANCE);
        addSimple(list, year, Month.MARCH,       3, "Bulgaria Liberation Day",      "BG Liberation", OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   22, "Bulgaria Day of Enlightenment","BG Enlightenment",OBSERVANCE);
        addSimple(list, year, Month.JUNE,       25, "Croatia Statehood Day",        "HR Statehood",  OBSERVANCE);
        addSimple(list, year, Month.AUGUST,      5, "Croatia Victory Day",          "HR Victory",    OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,   15, "Serbia Statehood Day",         "RS Statehood",  OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   11, "Latvia Independence Day",      "LV Independence",OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   16, "Latvia Proclamation Day",      "LV Proclamation",OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,   16, "Lithuania Independence Day",   "LT Independence",OBSERVANCE);
        addSimple(list, year, Month.MARCH,      11, "Lithuania Restoration Day",    "LT Restoration",OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,   24, "Estonia Independence Day",     "EE Independence",OBSERVANCE);
        addSimple(list, year, Month.JUNE,       23, "Midsummer Eve (Baltic)",       "Midsummer Eve", OBSERVANCE);
        addSimple(list, year, Month.JUNE,       24, "Midsummer / St. John's Day",   "Midsummer",     OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,    1, "Romania Great Union Day",      "RO Union Day",  OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     23, "Baltic Way Anniversary",       "Baltic Way",    OBSERVANCE);

        // ---- Orthodox Easter and related ----
        // Orthodox Easter uses the Julian calendar Computus, then adds 13 days for
        // the Gregorian equivalent (valid for 1900-2099).
        LocalDate orthodoxEaster = computeOrthodoxEaster(year);
        list.add(new Holiday(orthodoxEaster.minusDays(2), "Orthodox Good Friday",  "Orth. Good Fri", RELIGIOUS));
        list.add(new Holiday(orthodoxEaster.minusDays(1), "Orthodox Holy Saturday","Orth. Holy Sat", RELIGIOUS));
        list.add(new Holiday(orthodoxEaster,              "Orthodox Easter",       "Orth. Easter",   RELIGIOUS));
        list.add(new Holiday(orthodoxEaster.plusDays(1),  "Orthodox Easter Monday","Orth. Easter Mon",RELIGIOUS));
        list.add(new Holiday(orthodoxEaster.plusDays(49), "Orthodox Pentecost",    "Orth. Pentecost",RELIGIOUS));

        // Orthodox Christmas - Jan 7 (Gregorian equivalent of Dec 25 Julian)
        addSimple(list, year, Month.JANUARY, 7,  "Orthodox Christmas Day",        "Orth. Christmas",RELIGIOUS);
        addSimple(list, year, Month.JANUARY, 6,  "Orthodox Christmas Eve",        "Orth. Xmas Eve", RELIGIOUS);
        addSimple(list, year, Month.JANUARY, 19, "Orthodox Epiphany",             "Orth. Epiphany", RELIGIOUS);

        // Epiphany / Three Kings' Day - Jan 6 (observed across Catholic EE countries)
        addSimple(list, year, Month.JANUARY, 6,  "Epiphany (Three Kings' Day)",   "Epiphany",       RELIGIOUS);

        // All Saints' Day and All Souls' Day (widely observed in Catholic EE)
        addSimple(list, year, Month.NOVEMBER, 1, "All Saints' Day",               "All Saints",     RELIGIOUS);
        addSimple(list, year, Month.NOVEMBER, 2, "All Souls' Day",                "All Souls",      RELIGIOUS);

        // St. Nicholas Day - Dec 6 (Czech, Slovak, Polish, Hungarian tradition)
        addSimple(list, year, Month.DECEMBER, 6, "St. Nicholas Day",              "St. Nicholas",   RELIGIOUS);

        // Corpus Christi - 60 days after Easter (Poland, Czech, Slovakia, Croatia, Hungary)
        LocalDate catholicEaster = computeEaster(year);
        list.add(new Holiday(catholicEaster.plusDays(60),
            "Corpus Christi (EE)", "Corpus Christi", RELIGIOUS));
    }
}