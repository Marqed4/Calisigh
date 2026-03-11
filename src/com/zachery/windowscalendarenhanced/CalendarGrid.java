package com.zachery.windowscalendarenhanced;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CalendarGrid {
    private final YearMonth month;
    private final List<LocalDate> cells;

    public CalendarGrid(YearMonth input) {
        this.month = input;
        this.cells = generateCells(input);
    }

    public List<LocalDate> getCells() {
        return cells;
    }

    private List<LocalDate> generateCells(YearMonth input) {
        List<LocalDate> list = new ArrayList<>();

        LocalDate firstDay = input.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        LocalDate start = firstDay.minusDays(dayOfWeek - 1);

        for (int i = 0; i < 36; i++) {
            list.add(start.plusDays(i));
        }

        return list;
    }
}
