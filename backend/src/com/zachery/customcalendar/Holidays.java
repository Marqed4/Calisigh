package com.zachery.customcalendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class Holidays
{
    public static final String CATEGORY_FEDERAL       = "federal";
    public static final String CATEGORY_OBSERVANCE    = "observance";
    public static final String CATEGORY_RELIGIOUS     = "religious";
    public static final String CATEGORY_ASTRONOMICAL  = "astronomical";

    public static class Holiday
    {
        public final String date; // "YYYY-MM-DD"
        public final String name;
        public final String shortName;
        public final String category;

        Holiday(LocalDate date, String name, String shortName, String category)
        {
            this.date      = date.toString();
            this.name      = name;
            this.shortName = shortName;
            this.category  = category;
        }
    }

    // Returns all holidays for the given year across all categories.
    public static List<Holiday> forYear(int year)
    {
        List<Holiday> list = new ArrayList<>();
        addFederalHolidays(list, year);
        addObservances(list, year);
        addReligiousObservances(list, year);
        addAstronomicalDates(list, year);
        addEasternEuropeanHolidays(list, year);
        return list;
    }

    // Federal Holidays
    private static void addFederalHolidays(List<Holiday> list, int year)
    {
        // Fixed-date
        addFixed(list, year, Month.JANUARY,  1,  "New Year's Day",   "New Year's", CATEGORY_FEDERAL);
        addFixed(list, year, Month.JUNE,     19, "Juneteenth",       "Juneteenth", CATEGORY_FEDERAL);
        addFixed(list, year, Month.JULY,     4,  "Independence Day", "Indep. Day", CATEGORY_FEDERAL);
        addFixed(list, year, Month.NOVEMBER, 11, "Veterans Day",     "Veterans",   CATEGORY_FEDERAL);
        addFixed(list, year, Month.DECEMBER, 25, "Christmas Day",    "Christmas",  CATEGORY_FEDERAL);

        // Floating
        list.add(new Holiday(nthWeekday(year, Month.JANUARY,   DayOfWeek.MONDAY,   3),
            "Martin Luther King Jr. Day", "MLK Day",      CATEGORY_FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.FEBRUARY,  DayOfWeek.MONDAY,   3),
            "Presidents' Day",            "Pres. Day",    CATEGORY_FEDERAL));
        list.add(new Holiday(lastWeekday(year, Month.MAY,      DayOfWeek.MONDAY),
            "Memorial Day",               "Memorial",     CATEGORY_FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.SEPTEMBER, DayOfWeek.MONDAY,   1),
            "Labor Day",                  "Labor Day",    CATEGORY_FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.OCTOBER,   DayOfWeek.MONDAY,   2),
            "Columbus Day",               "Columbus",     CATEGORY_FEDERAL));
        list.add(new Holiday(nthWeekday(year, Month.NOVEMBER,  DayOfWeek.THURSDAY, 4),
            "Thanksgiving",               "Thanksgiving", CATEGORY_FEDERAL));

        // Election Day – 1st Tuesday after the 1st Monday in November
        LocalDate firstMondayNov = nthWeekday(year, Month.NOVEMBER, DayOfWeek.MONDAY, 1);
        list.add(new Holiday(firstMondayNov.plusDays(1),
            "Election Day", "Election", CATEGORY_FEDERAL));

        // Armed Forces Day – 3rd Saturday in May
        list.add(new Holiday(nthWeekday(year, Month.MAY, DayOfWeek.SATURDAY, 3),
            "Armed Forces Day", "Armed Forces", CATEGORY_FEDERAL));

        // Indigenous Peoples' Day – same floating date as Columbus Day (2nd Monday in October)
        list.add(new Holiday(nthWeekday(year, Month.OCTOBER, DayOfWeek.MONDAY, 2),
            "Indigenous Peoples' Day", "Indigenous", CATEGORY_FEDERAL));
    }

    // Cultural Observances
    private static void addObservances(List<Holiday> list, int year)
    {
        // Fixed-date observances
        addSimple(list, year, Month.FEBRUARY,  2,  "Groundhog Day",           "Groundhog",    CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,  14, "Valentine's Day",         "Valentine's",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MARCH,     17, "St. Patrick's Day",       "St. Pat's",    CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.APRIL,     22, "Earth Day",               "Earth Day",    CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MAY,       5,  "Cinco de Mayo",           "Cinco Mayo",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JUNE,      19, "Juneteenth (Obs.)",       "Juneteenth",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JUNE,      26, "Trans Pride Day",         "Trans Pride",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   16, "Spirit Day",              "Spirit Day",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   31, "Halloween",               "Halloween",    CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,  1,  "Día de los Muertos",      "Día Muertos",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,  2,  "Día de los Muertos",      "Día Muertos",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  24, "Christmas Eve",           "Xmas Eve",     CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  31, "New Year's Eve",          "NYE",          CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MARCH,     14, "Pi Day",                  "Pi Day",       CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.APRIL,     1,  "April Fools' Day",        "April Fools",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.APRIL,     15, "Tax Day",                 "Tax Day",      CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.APRIL,     20, "420",                     "420",          CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MAY,       4,  "Star Wars Day",           "May the 4th",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MAY,       19, "Malcolm X Day",           "Malcolm X",    CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MAY,       22, "Harvey Milk Day",         "Harvey Milk",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JUNE,      14, "Flag Day",                "Flag Day",     CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER, 11, "Patriot Day",             "Patriot Day",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER, 23, "Bisexual Visibility Day", "Bi Visibility",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   11, "National Coming Out Day", "Coming Out",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,   26, "Intersex Awareness Day",  "Intersex Day", CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,  20, "Transgender Day of Remembrance", "Trans Rememb.", CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  7,  "Pearl Harbor Remembrance Day", "Pearl Harbor", CATEGORY_OBSERVANCE);

        // Dec 1 has two observances on the same date
        addSimple(list, year, Month.DECEMBER,  1,  "Rosa Parks Day",          "Rosa Parks",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,  1,  "World AIDS Day",          "AIDS Day",     CATEGORY_OBSERVANCE);

        // Boss's Day
        addSimple(list, year, Month.OCTOBER,   16, "Boss's Day",              "Boss's Day",   CATEGORY_OBSERVANCE);

        // Heritage / awareness month starts
        addSimple(list, year, Month.FEBRUARY,  1,  "Black History Month",     "BH Month",     CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MARCH,     1,  "Women's History Month",   "WHM",          CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JUNE,      1,  "Pride Month",             "Pride Month",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER, 15, "Hispanic Heritage Month", "Hisp. Heritage", CATEGORY_OBSERVANCE);

        // Dynamic observances
        // Mother's Day – 2nd Sunday of May
        list.add(new Holiday(nthWeekday(year, Month.MAY,  DayOfWeek.SUNDAY, 2),
            "Mother's Day", "Mother's", CATEGORY_OBSERVANCE));

        // Father's Day – 3rd Sunday of June
        list.add(new Holiday(nthWeekday(year, Month.JUNE, DayOfWeek.SUNDAY, 3),
            "Father's Day", "Father's", CATEGORY_OBSERVANCE));

        // Mardi Gras – 47 days before Easter (Fat Tuesday)
        LocalDate easter = computeEaster(year);
        list.add(new Holiday(easter.minusDays(47),
            "Mardi Gras", "Mardi Gras", CATEGORY_OBSERVANCE));

        // Administrative Professionals' Day – last Wednesday of April
        list.add(new Holiday(lastWeekday(year, Month.APRIL, DayOfWeek.WEDNESDAY),
            "Administrative Professionals' Day", "Admin Day", CATEGORY_OBSERVANCE));

        // Grandparents' Day – 1st Sunday after Labor Day
        LocalDate laborDay = nthWeekday(year, Month.SEPTEMBER, DayOfWeek.MONDAY, 1);
        list.add(new Holiday(laborDay.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)),
            "Grandparents' Day", "Grandparents'", CATEGORY_OBSERVANCE));

        // Daylight Saving Time start – 2nd Sunday in March
        list.add(new Holiday(nthWeekday(year, Month.MARCH, DayOfWeek.SUNDAY, 2),
            "Daylight Saving Time Begins", "DST Start", CATEGORY_OBSERVANCE));

        // Daylight Saving Time end – 1st Sunday in November
        list.add(new Holiday(nthWeekday(year, Month.NOVEMBER, DayOfWeek.SUNDAY, 1),
            "Daylight Saving Time Ends", "DST End", CATEGORY_OBSERVANCE));

        // Lunar New Year – 2nd new moon after Dec 21 solstice (1st new moon after Jan 21)
        list.add(new Holiday(computeLunarNewYear(year),
            "Lunar New Year", "Lunar NY", CATEGORY_OBSERVANCE));

        // Lantern Festival – 15 days after Lunar New Year
        list.add(new Holiday(computeLunarNewYear(year).plusDays(15),
            "Lantern Festival", "Lantern Fest", CATEGORY_OBSERVANCE));

        // Mid-Autumn Festival – 15th day of the 8th lunar month (~Sept/Oct full moon)
        list.add(new Holiday(computeMidAutumnFestival(year),
            "Mid-Autumn Festival", "Mid-Autumn", CATEGORY_OBSERVANCE));
    }

    // Religious Observances
    private static void addReligiousObservances(List<Holiday> list, int year)
    {
        LocalDate easter = computeEaster(year);

        // Easter-relative Christian dates
        list.add(new Holiday(easter.minusDays(46), "Ash Wednesday", "Ash Wed",   CATEGORY_RELIGIOUS));
        list.add(new Holiday(easter.minusDays(7),  "Palm Sunday",   "Palm Sun",  CATEGORY_RELIGIOUS));
        list.add(new Holiday(easter.minusDays(3),  "Holy Thursday", "Holy Thu",  CATEGORY_RELIGIOUS));
        list.add(new Holiday(easter.minusDays(2),  "Good Friday",   "Good Fri",  CATEGORY_RELIGIOUS));
        list.add(new Holiday(easter.minusDays(1),  "Holy Saturday", "Holy Sat",  CATEGORY_RELIGIOUS));
        list.add(new Holiday(easter,               "Easter Sunday", "Easter",    CATEGORY_RELIGIOUS));
        list.add(new Holiday(easter.plusDays(39),  "Ascension Day", "Ascension", CATEGORY_RELIGIOUS));
        list.add(new Holiday(easter.plusDays(49),  "Pentecost",     "Pentecost", CATEGORY_RELIGIOUS));

        // Fixed-date Christian
        addSimple(list, year, Month.DECEMBER, 24, "Christmas Eve",   "Xmas Eve",  CATEGORY_RELIGIOUS);
        addSimple(list, year, Month.DECEMBER, 25, "Christmas Day",   "Christmas", CATEGORY_RELIGIOUS);
        addSimple(list, year, Month.DECEMBER, 26, "Kwanzaa Begins",  "Kwanzaa",   CATEGORY_RELIGIOUS);

        // Hanukkah – 25 Kislev (8 nights)
        LocalDate hanukkah = computeHanukkah(year);
        for (int i = 0; i < 8; i++)
        {
            LocalDate day   = hanukkah.plusDays(i);
            String    label = i == 0 ? "Hanukkah Begins" : (i == 7 ? "Hanukkah Ends" : "Hanukkah");
            list.add(new Holiday(day, label, "Hanukkah", CATEGORY_RELIGIOUS));
        }

        // Jewish high holidays and festivals (Hebrew calendar)
        addJewishHolidays(list, year);

        // Islamic holidays (Hijri calendar)
        addIslamicHolidays(list, year);

        // Hindu/South Asian festivals
        addHinduHolidays(list, year);
    }

    // Jewish Holidays
    // Uses the same Hebrew-calendar anchor approach as Hanukkah.
    // All dates are offsets in days from 1 Tishri (Rosh Hashanah).
    //   1 Tishri  = Rosh Hashanah day 1
    //   2 Tishri  = Rosh Hashanah day 2
    //  10 Tishri  = Yom Kippur
    //  15 Tishri  = Sukkot begins  (7 days)
    //  22 Tishri  = Shemini Atzeret / Simchat Torah
    // Nisan 15    = Passover (Pesach) first night   – Tishri is month 1, Nisan is month 7 in civil order
    //               offset from Rosh Hashanah ≈ +163 days (varies ±1 by year type)
    // Sivan 6     = Shavuot  – offset from Pesach + 50 days (= ~+213 from Tishri)
    // Adar 14     = Purim    – falls ~30 days before Pesach (offset ~+133 from Tishri)
    private static void addJewishHolidays(List<Holiday> list, int year)
    {
        LocalDate roshHashanah = computeRoshHashanah(year);

        // Rosh Hashanah – 2 days
        list.add(new Holiday(roshHashanah,           "Rosh Hashanah",   "Rosh Hash.",  CATEGORY_RELIGIOUS));
        list.add(new Holiday(roshHashanah.plusDays(1), "Rosh Hashanah (day 2)", "Rosh Hash.", CATEGORY_RELIGIOUS));

        // Yom Kippur – 10 Tishri
        list.add(new Holiday(roshHashanah.plusDays(9), "Yom Kippur", "Yom Kippur", CATEGORY_RELIGIOUS));

        // Sukkot – 15 Tishri, 7 days
        LocalDate sukkot = roshHashanah.plusDays(14);
        for (int i = 0; i < 7; i++)
        {
            String label = i == 0 ? "Sukkot Begins" : (i == 6 ? "Sukkot Ends" : "Sukkot");
            list.add(new Holiday(sukkot.plusDays(i), label, "Sukkot", CATEGORY_RELIGIOUS));
        }

        // Shemini Atzeret / Simchat Torah – 22 Tishri
        list.add(new Holiday(roshHashanah.plusDays(21), "Shemini Atzeret / Simchat Torah", "Simchat Torah", CATEGORY_RELIGIOUS));

        // Purim – 14 Adar, approximately 30 days before Passover
        // Passover (Pesach) – 15 Nisan, ~163 days after Rosh Hashanah (varies by year type)
        // We compute Passover from the spring full moon after Rosh Hashanah + ~6 months
        LocalDate passover = computePassover(year);
        list.add(new Holiday(passover.minusDays(30), "Purim",               "Purim",    CATEGORY_RELIGIOUS));
        list.add(new Holiday(passover,               "Passover (Pesach)",   "Passover", CATEGORY_RELIGIOUS));
        list.add(new Holiday(passover.plusDays(6),   "Passover ends",       "Passover", CATEGORY_RELIGIOUS));

        // Shavuot – 6 Sivan, exactly 50 days after first day of Passover (day after the 7 weeks of Omer)
        list.add(new Holiday(passover.plusDays(50),  "Shavuot",             "Shavuot",  CATEGORY_RELIGIOUS));
    }

    /**
     * Computes Rosh Hashanah (1 Tishri) for the civil year.
     * Tishri falls in September/October.
     * Uses the same anchor-based approach as computeHanukkah().
     * Anchor: Rosh Hashanah 5761 = Sep 30, 2000.
     */
    static LocalDate computeRoshHashanah(int year)
    {
        final int       ANCHOR_YEAR = 2000;
        final LocalDate ANCHOR      = LocalDate.of(2000, 9, 30);
        final double    HY_DAYS     = 365.24682220903;

        long diff = Math.round((year - ANCHOR_YEAR) * HY_DAYS);
        LocalDate approx = ANCHOR.plusDays(diff);

        // Rosh Hashanah is always in September or October; nudge if we drifted
        if (approx.getMonthValue() < 8)  approx = approx.plusDays(Math.round(HY_DAYS));
        if (approx.getMonthValue() > 10) approx = approx.minusDays(Math.round(HY_DAYS));

        return approx;
    }

    /**
     * Computes Passover (15 Nisan) for the civil year.
     * Passover falls on the first full moon on or after the spring equinox in March/April,
     * approximately 163 days after Rosh Hashanah of the previous Hebrew year.
     * Anchor: Passover 5761 = Apr 8, 2001.
     */
    static LocalDate computePassover(int year)
    {
        final int       ANCHOR_YEAR = 2001;
        final LocalDate ANCHOR      = LocalDate.of(2001, 4, 8);
        final double    HY_DAYS     = 365.24682220903;

        long diff = Math.round((year - ANCHOR_YEAR) * HY_DAYS);
        LocalDate approx = ANCHOR.plusDays(diff);

        // Passover is always in March or April
        if (approx.getMonthValue() < 3)  approx = approx.plusDays(Math.round(HY_DAYS));
        if (approx.getMonthValue() > 4)  approx = approx.minusDays(Math.round(HY_DAYS));

        return approx;
    }

    // Islamic Holidays  (Hijri calendar)
    // The Islamic year is purely lunar: 12 months × ~29.53 days ≈ 354.367 days.
    // Each Gregorian year, Islamic dates shift ~10–11 days earlier.
    //
    // Key Hijri dates:
    //   1 Muharram   = Islamic New Year
    //  12 Rabi al-Awwal = Mawlid al-Nabi  (+11 days after New Year)
    //   1 Ramadan    = Ramadan start       (+148 days after New Year approx)
    //   1 Shawwal    = Eid al-Fitr         (+178 days after New Year approx)
    //  10 Dhu al-Hijjah = Eid al-Adha      (+318 days after New Year approx)
    //
    // Anchor: 1 Muharram 1422 AH = March 26, 2001 CE.
    private static void addIslamicHolidays(List<Holiday> list, int year)
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

            addIslamicSet(list, newYear, year);
        }
    }

    private static void addIslamicSet(List<Holiday> list, LocalDate newYear, int targetYear)
    {
        addIfInYear(list, newYear,              "Islamic New Year",           "Islamic NY",  CATEGORY_RELIGIOUS, targetYear);
        addIfInYear(list, newYear.plusDays(11), "Mawlid al-Nabi",             "Mawlid",      CATEGORY_RELIGIOUS, targetYear);
        addIfInYear(list, newYear.plusDays(148),"Ramadan Begins",             "Ramadan",     CATEGORY_RELIGIOUS, targetYear);
        addIfInYear(list, newYear.plusDays(178),"Eid al-Fitr",                "Eid al-Fitr", CATEGORY_RELIGIOUS, targetYear);
        addIfInYear(list, newYear.plusDays(179),"Eid al-Fitr (day 2)",        "Eid al-Fitr", CATEGORY_RELIGIOUS, targetYear);
        addIfInYear(list, newYear.plusDays(317),"Eid al-Adha",                "Eid al-Adha", CATEGORY_RELIGIOUS, targetYear);
        addIfInYear(list, newYear.plusDays(318),"Eid al-Adha (day 2)",        "Eid al-Adha", CATEGORY_RELIGIOUS, targetYear);
    }

    /** Only adds the holiday if its date falls within the given target year. */
    private static void addIfInYear(List<Holiday> list, LocalDate date, String name,
                                    String shortName, String category, int targetYear)
    {
        if (date.getYear() == targetYear)
            list.add(new Holiday(date, name, shortName, category));
    }

    // Hindu / South Asian Holidays
    //
    // These use astronomical approximations anchored to known dates.
    //
    //  Diwali     – 15th day of Kartik (new moon of Kartik); falls Oct 13 – Nov 15
    //               Anchor: Diwali 2000 = Oct 26, 2000
    //  Holi       – last full moon of Phalguna; falls Feb 20 – Mar 22
    //               Anchor: Holi 2000 = Mar 20, 2000  (Holika Dahan eve)
    //  Dussehra   – 10th day (Vijaya Dashami) of Navratri; falls Sep 22 – Oct 24
    //               Anchor: Dussehra 2000 = Oct 7, 2000
    //
    // The Hindu lunisolar year averages ~365.2587 days.
    private static void addHinduHolidays(List<Holiday> list, int year)
    {
        final double HINDU_YEAR = 365.25636; // sidereal year used by Hindu calendar

        // Diwali
        final LocalDate DIWALI_ANCHOR = LocalDate.of(2000, 10, 26);
        LocalDate diwali = shiftByYear(DIWALI_ANCHOR, year, 2000, HINDU_YEAR);
        // Diwali is always in Oct or Nov
        if (diwali.getMonthValue() < 9)  diwali = diwali.plusDays(Math.round(HINDU_YEAR));
        if (diwali.getMonthValue() > 11) diwali = diwali.minusDays(Math.round(HINDU_YEAR));
        list.add(new Holiday(diwali, "Diwali", "Diwali", CATEGORY_RELIGIOUS));

        // Holi (Holika Dahan – the main bonfire night, eve of the color festival)
        final LocalDate HOLI_ANCHOR = LocalDate.of(2000, 3, 20);
        LocalDate holi = shiftByYear(HOLI_ANCHOR, year, 2000, HINDU_YEAR);
        if (holi.getMonthValue() < 2)  holi = holi.plusDays(Math.round(HINDU_YEAR));
        if (holi.getMonthValue() > 3)  holi = holi.minusDays(Math.round(HINDU_YEAR));
        list.add(new Holiday(holi,          "Holi (Holika Dahan)", "Holi",      CATEGORY_RELIGIOUS));
        list.add(new Holiday(holi.plusDays(1), "Holi",             "Holi",      CATEGORY_RELIGIOUS));

        // Dussehra / Vijaya Dashami
        final LocalDate DUSSEHRA_ANCHOR = LocalDate.of(2000, 10, 7);
        LocalDate dussehra = shiftByYear(DUSSEHRA_ANCHOR, year, 2000, HINDU_YEAR);
        if (dussehra.getMonthValue() < 9)  dussehra = dussehra.plusDays(Math.round(HINDU_YEAR));
        if (dussehra.getMonthValue() > 10) dussehra = dussehra.minusDays(Math.round(HINDU_YEAR));
        list.add(new Holiday(dussehra, "Dussehra (Vijaya Dashami)", "Dussehra", CATEGORY_RELIGIOUS));
    }

    private static LocalDate shiftByYear(LocalDate anchor, int year, int anchorYear, double yearDays)
    {
        long diff = Math.round((year - anchorYear) * yearDays);
        return anchor.plusDays(diff);
    }

    // Astronomical Dates
    private static void addAstronomicalDates(List<Holiday> list, int year)
    {
        // Approximate fixed dates accurate to ±1 day for the near future.
        // For precise times, use an astronomical library.
        addSimple(list, year, Month.MARCH,     20, "Spring Equinox",   "Spr. Equinox", CATEGORY_ASTRONOMICAL);
        addSimple(list, year, Month.JUNE,      21, "Summer Solstice",  "Sum. Solstice",CATEGORY_ASTRONOMICAL);
        addSimple(list, year, Month.SEPTEMBER, 23, "Autumnal Equinox", "Fall Equinox", CATEGORY_ASTRONOMICAL);
        addSimple(list, year, Month.DECEMBER,  21, "Winter Solstice",  "Win. Solstice",CATEGORY_ASTRONOMICAL);
    }

    // Eastern European Holidays
    // Covers widely observed public holidays across Poland, Ukraine, Czech Republic,
    // Slovakia, Hungary, Romania, Bulgaria, Croatia, Serbia, and the Baltic states.
    private static void addEasternEuropeanHolidays(List<Holiday> list, int year)
    {
        // ---- Fixed civil holidays common across the region ----
        addSimple(list, year, Month.JANUARY,   1,  "New Year's Day (EE)",          "NY (EE)",       CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MAY,        1,  "International Workers' Day",   "Workers' Day",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MAY,        8,  "Victory in Europe Day (EE)",   "VE Day (EE)",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   11, "Independence Day (Poland)",    "PL Independence",CATEGORY_OBSERVANCE);

        // ---- Specific national days ----
        addSimple(list, year, Month.MARCH,      15, "Hungarian National Day",       "HU Natl Day",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     20, "St. Stephen's Day (Hungary)",  "HU St. Stephen",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,    23, "Hungarian Republic Day",       "HU Republic",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.SEPTEMBER,  28, "Czech Statehood Day",          "CZ Statehood",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,    28, "Czech Independence Day",       "CZ Independence",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JANUARY,    17, "Slovak Statehood Day",         "SK Statehood",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     29, "Slovak National Uprising Day", "SK Uprising",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     24, "Ukraine Independence Day",     "UA Independence",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.OCTOBER,    14, "Defender of Ukraine Day",      "UA Defender",   CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MARCH,       3, "Bulgaria Liberation Day",      "BG Liberation", CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   22, "Bulgaria Day of Enlightenment","BG Enlightenment",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JUNE,       25, "Croatia Statehood Day",        "HR Statehood",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.AUGUST,      5, "Croatia Victory Day",          "HR Victory",    CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,   15, "Serbia Statehood Day",         "RS Statehood",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   11, "Latvia Independence Day",      "LV Independence",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.NOVEMBER,   16, "Latvia Proclamation Day",      "LV Proclamation",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,   16, "Lithuania Independence Day",   "LT Independence",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.MARCH,      11, "Lithuania Restoration Day",    "LT Restoration",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.FEBRUARY,   24, "Estonia Independence Day",     "EE Independence",CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JUNE,       23, "Midsummer Eve (Baltic)",       "Midsummer Eve", CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.JUNE,       24, "Midsummer / St. John's Day",   "Midsummer",     CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.DECEMBER,    1, "Romania Great Union Day",      "RO Union Day",  CATEGORY_OBSERVANCE);
        addSimple(list, year, Month.AUGUST,     23, "Baltic Way Anniversary",       "Baltic Way",    CATEGORY_OBSERVANCE);

        // ---- Orthodox Easter and related ----
        // Orthodox Easter uses the Julian calendar Computus, then adds 13 days for
        // the Gregorian equivalent (valid for 1900–2099).
        LocalDate orthodoxEaster = computeOrthodoxEaster(year);
        list.add(new Holiday(orthodoxEaster.minusDays(2), "Orthodox Good Friday",  "Orth. Good Fri", CATEGORY_RELIGIOUS));
        list.add(new Holiday(orthodoxEaster.minusDays(1), "Orthodox Holy Saturday","Orth. Holy Sat", CATEGORY_RELIGIOUS));
        list.add(new Holiday(orthodoxEaster,              "Orthodox Easter",       "Orth. Easter",   CATEGORY_RELIGIOUS));
        list.add(new Holiday(orthodoxEaster.plusDays(1),  "Orthodox Easter Monday","Orth. Easter Mon",CATEGORY_RELIGIOUS));
        list.add(new Holiday(orthodoxEaster.plusDays(49), "Orthodox Pentecost",    "Orth. Pentecost",CATEGORY_RELIGIOUS));

        // Orthodox Christmas – Jan 7 (Gregorian equivalent of Dec 25 Julian)
        addSimple(list, year, Month.JANUARY, 7,  "Orthodox Christmas Day",        "Orth. Christmas",CATEGORY_RELIGIOUS);
        addSimple(list, year, Month.JANUARY, 6,  "Orthodox Christmas Eve",        "Orth. Xmas Eve", CATEGORY_RELIGIOUS);
        addSimple(list, year, Month.JANUARY, 19, "Orthodox Epiphany",             "Orth. Epiphany", CATEGORY_RELIGIOUS);

        // Epiphany / Three Kings' Day – Jan 6 (observed across Catholic EE countries)
        addSimple(list, year, Month.JANUARY, 6,  "Epiphany (Three Kings' Day)",   "Epiphany",       CATEGORY_RELIGIOUS);

        // All Saints' Day and All Souls' Day (widely observed in Catholic EE)
        addSimple(list, year, Month.NOVEMBER, 1, "All Saints' Day",               "All Saints",     CATEGORY_RELIGIOUS);
        addSimple(list, year, Month.NOVEMBER, 2, "All Souls' Day",                "All Souls",      CATEGORY_RELIGIOUS);

        // St. Nicholas Day – Dec 6 (Czech, Slovak, Polish, Hungarian tradition)
        addSimple(list, year, Month.DECEMBER, 6, "St. Nicholas Day",              "St. Nicholas",   CATEGORY_RELIGIOUS);

        // Corpus Christi – 60 days after Easter (Poland, Czech, Slovakia, Croatia, Hungary)
        LocalDate catholicEaster = computeEaster(year);
        list.add(new Holiday(catholicEaster.plusDays(60),
            "Corpus Christi (EE)", "Corpus Christi", CATEGORY_RELIGIOUS));
    }

    /**
     * Computes Orthodox Easter (Gregorian date) using the Julian Computus
     * with a +13-day correction for the 20th–21st century.
     * Valid for years 1900–2099.
     */
    static LocalDate computeOrthodoxEaster(int year)
    {
        // Julian Easter via the "Old Calendar" algorithm
        int a = year % 4;
        int b = year % 7;
        int c = year % 19;
        int d = (19 * c + 15) % 30;
        int e = (2 * a + 4 * b - d + 34) % 7;
        int month = (d + e + 114) / 31;   // 3 = March, 4 = April
        int day   = (d + e + 114) % 31 + 1;

        LocalDate julianEaster = LocalDate.of(year, month, day);
        // Convert Julian to Gregorian: +13 days for 1900–2099
        return julianEaster.plusDays(13);
    }

    // Lunar Calendar Helpers (Chinese / East Asian)

    /**
     * Computes Lunar New Year (Chinese New Year) for the given Gregorian year.
     * LNY is the 2nd new moon after the December solstice, which is the same as
     * the 1st new moon after January 21.
     * We use a synodic month anchor: new moon of Jan 27, 2000 (known LNY 2000 date).
     * Mean synodic month = 29.53058868 days.
     */
    static LocalDate computeLunarNewYear(int year)
    {
        final double SYNODIC_MONTH  = 29.53058868;
        // Anchor: Lunar New Year 2000 = February 5, 2000
        final long   ANCHOR_EPOCH   = LocalDate.of(2000, 2, 5).toEpochDay();
        // Lunar year ≈ 12 synodic months = 354.36707 days; every 19 Gregorian years = 235 lunar months
        // Average lunar years per Gregorian year is slightly less than 1.
        // Simplest: find the new moon in late Jan/early Feb of the target year.
        double lunarYearsSince = (year - 2000) * (12.0 + 7.0 / 19.0) / 12.0;
        long approxOffset = Math.round(lunarYearsSince * 12 * SYNODIC_MONTH);
        LocalDate approx = LocalDate.ofEpochDay(ANCHOR_EPOCH + approxOffset);

        // Snap to the nearest date that is between Jan 21 and Feb 20 (LNY window)
        while (approx.getMonthValue() == 1 && approx.getDayOfMonth() < 21)
            approx = approx.plusDays(Math.round(SYNODIC_MONTH));
        while (approx.getMonthValue() > 2 || (approx.getMonthValue() == 2 && approx.getDayOfMonth() > 20))
            approx = approx.minusDays(Math.round(SYNODIC_MONTH));

        return approx;
    }

    /**
     * Computes the Mid-Autumn Festival (15th day of the 8th lunar month).
     * This is always the full moon nearest to the autumnal equinox, falling in Sep/Oct.
     * Anchor: Mid-Autumn 2000 = September 12, 2000.
     */
    static LocalDate computeMidAutumnFestival(int year)
    {
        final double   SYNODIC_MONTH = 29.53058868;
        final LocalDate ANCHOR       = LocalDate.of(2000, 9, 12);
        // 12 lunar months per lunar year; scale to Gregorian
        long diff = Math.round((year - 2000) * 12.368266 * SYNODIC_MONTH);
        LocalDate approx = ANCHOR.plusDays(diff);

        // Mid-Autumn is always in September or October
        if (approx.getMonthValue() < 8)  approx = approx.plusDays(Math.round(SYNODIC_MONTH));
        if (approx.getMonthValue() > 10) approx = approx.minusDays(Math.round(SYNODIC_MONTH));

        return approx;
    }

    // Easter Algorithm

    /**
     * Computes Easter Sunday for the given year using the Anonymous Gregorian algorithm!
     */
    static LocalDate computeEaster(int year)
    {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day   = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }

    // Hanukkah Algorithm

    /**
     * Approximates the Gregorian date of the first night of Hanukkah (25 Kislev)
     * using the Hebrew calendar epoch offset.
     */
    static LocalDate computeHanukkah(int year)
    {
        int ANCHOR_YEAR    = 2000;
        LocalDate ANCHOR   = LocalDate.of(2000, 12, 22);
        double HEBREW_YEAR_DAYS = 365.24682220903;
        long diff = Math.round((year - ANCHOR_YEAR) * HEBREW_YEAR_DAYS);
        LocalDate approx = ANCHOR.plusDays(diff);

        if (approx.getMonthValue() == 1)  approx = approx.minusDays(Math.round(HEBREW_YEAR_DAYS));
        if (approx.getMonthValue() == 11 && approx.getDayOfMonth() < 20)
            approx = approx.plusDays(Math.round(HEBREW_YEAR_DAYS));

        return approx;
    }

    // Utility helpers

    /** Fixed-date holiday with observed date for federal holidays. */
    private static void addFixed(List<Holiday> list, int year, Month month, int day,
                                 String name, String shortName, String category)
    {
        LocalDate actual = LocalDate.of(year, month, day);
        list.add(new Holiday(actual, name, shortName, category));

        DayOfWeek dow = actual.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY)
            list.add(new Holiday(actual.minusDays(1), name + " (Observed)", shortName + "*", category));
        else if (dow == DayOfWeek.SUNDAY)
            list.add(new Holiday(actual.plusDays(1),  name + " (Observed)", shortName + "*", category));
    }

    /** Simple fixed-date entry with no observed-date shift. */
    private static void addSimple(List<Holiday> list, int year, Month month, int day,
                                  String name, String shortName, String category)
    {
        list.add(new Holiday(LocalDate.of(year, month, day), name, shortName, category));
    }

    private static LocalDate nthWeekday(int year, Month month, DayOfWeek dow, int n)
    {
        return LocalDate.of(year, month, 1)
            .with(TemporalAdjusters.nextOrSame(dow))
            .plusWeeks(n - 1);
    }

    private static LocalDate lastWeekday(int year, Month month, DayOfWeek dow)
    {
        return LocalDate.of(year, month, 1)
            .with(TemporalAdjusters.lastDayOfMonth())
            .with(TemporalAdjusters.previousOrSame(dow));
    }
}