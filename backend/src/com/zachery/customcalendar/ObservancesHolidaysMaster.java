package com.zachery.customcalendar;

import java.util.ArrayList;
import java.util.List;

import com.zachery.customcalendar.ObservationsHolidays.AstronomicalDates;
import com.zachery.customcalendar.ObservationsHolidays.EasternEuropeanHolidays;
import com.zachery.customcalendar.ObservationsHolidays.FederalHolidays;
import com.zachery.customcalendar.ObservationsHolidays.Holiday;
import com.zachery.customcalendar.ObservationsHolidays.Observances;
import com.zachery.customcalendar.ObservationsHolidays.ReligiousHolidays;

public class ObservancesHolidaysMaster
{
    public static List<Holiday> forYear(int year)
    {
        List<Holiday> list = new ArrayList<>();
        FederalHolidays.addAll(list, year);
        Observances.addAll(list, year);
        ReligiousHolidays.addAll(list, year);
        AstronomicalDates.addAll(list, year);
        EasternEuropeanHolidays.addAll(list, year);
        return list;
    }
}