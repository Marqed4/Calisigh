package com.zachery.windowscalendarenhanced;

import javafx.scene.layout.*;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CalendarUI extends StackPane {

    //Grid pane is a pane of grids.
    //By default, it does not implement cells, gridlines,
    //and it doesn't distribute the sizes equally.
    private GridPane grid;

    public CalendarUI() {
        grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);

        for (int i = 0; i < 6; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 6);
            grid.getColumnConstraints().add(col);
        }

        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 6);
            grid.getRowConstraints().add(row);
        }

        this.getStylesheets().add(
                getClass().getResource("/CalendarUI.css").toExternalForm()
        );

        CalendarGrid gridModel = new CalendarGrid(YearMonth.now());
        List<LocalDate> cells = gridModel.getCells();

        for (int i = 0; i < 36; i++) {
            LocalDate date = cells.get(i);
            VBox cell = createCell(date);
            int row = i / 6;
            int col = i % 6;
            grid.add(cell, col, row);
        }

        VBox container = new VBox();
        container.getChildren().addAll(createHeader(), grid);
        this.getChildren().add(container);
    }

    private HBox createHeader() {
        HBox header = new HBox();

        String[] days = new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (String day : days) {
            Label dayLabel = new Label(day);
            dayLabel.getStyleClass().add("calendar-header-label");
            HBox.setHgrow(dayLabel, Priority.ALWAYS);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            header.getChildren().add(dayLabel);
        }

        return header;
    }

    private VBox createCell(LocalDate date) {
        Label label = new Label(String.valueOf(date.getDayOfMonth()));
        VBox box = new VBox(label);
        box.getStyleClass().add("calendar-cell");
        box.setPrefSize(100, 80);

        if (date.equals(LocalDate.now())) {
            box.getStyleClass().add("today-cell");
        }

        return box;
    }

    public GridPane getView() {
        return grid;
    }
}