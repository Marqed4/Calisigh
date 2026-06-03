package com.zachery.customcalendar.ObservationsHolidays;

import java.time.LocalDate;

/**
 * Self-contained calendar-conversion and astronomical-approximation algorithms.
 * Kept together because they are the trickiest, most test-worthy part of the
 * codebase and are shared across multiple providers (Easter alone is used by
 * three of them).
 *
 * Methods are package-private; callers all live in com.zachery.customcalendar.
 */
final class Computus
{
    private Computus() {}

    // Easter

    /**
     * Computes Easter Sunday for the given year using the Anonymous Gregorian
     * algorithm.
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

    /**
     * Computes Orthodox Easter (Gregorian date) using the Julian Computus
     * with a +13-day correction for the 20th-21st century.
     * Valid for years 1900-2099.
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
        // Convert Julian to Gregorian: +13 days for 1900-2099
        return julianEaster.plusDays(13);
    }

    // Hebrew calendar

    /**
     * Approximates the Gregorian date of the first night of Hanukkah (25 Kislev)
     * using the Hebrew calendar epoch offset.
     */
    static LocalDate computeHanukkah(int year)
    {
        int ANCHOR_YEAR = 2000;
        LocalDate ANCHOR = LocalDate.of(2000, 12, 22);
        double HEBREW_YEAR_DAYS = 365.24682220903;
        long diff = Math.round((year - ANCHOR_YEAR) * HEBREW_YEAR_DAYS);
        LocalDate approx = ANCHOR.plusDays(diff);

        if (approx.getMonthValue() == 1) approx = approx.minusDays(Math.round(HEBREW_YEAR_DAYS));
        if (approx.getMonthValue() == 11 && approx.getDayOfMonth() < 20)
            approx = approx.plusDays(Math.round(HEBREW_YEAR_DAYS));

        return approx;
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
     * Passover falls on the first full moon on or after the spring equinox in
     * March/April, approximately 163 days after Rosh Hashanah of the previous
     * Hebrew year.
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

    // Lunar calendar (Chinese / East Asian)

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
        // Lunar year ~= 12 synodic months = 354.36707 days; every 19 Gregorian years = 235 lunar months
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
}