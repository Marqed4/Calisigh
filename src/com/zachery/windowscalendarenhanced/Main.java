package com.zachery.windowscalendarenhanced;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
todo
Priority:

2. Up & Down buttons to switch/navigate between weeks.
3. Allow the right clicking on the pane shows event menu.
4. Ability to view calendar by Day/ Week/ Year.

Luxury:
1. Bottom left of screen allows an email to be sent.
2. Seven day forecast, showing weather conditions of the following seven days.
3.
*/

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage application) {
        Group root = new Group();

        /*
        todo
        Starting size should be relative to native monitor size e.g., 1175 x 865 = native: 2560 x 1440
        */

        //Matching 2560 x 1440 specifications
        Scene scene = new Scene(root, 1175, 865, Color.LIGHTGRAY);

        CalendarUI cal  = new CalendarUI();
        root.getChildren().add(cal);

        application.setTitle("Windows Calendar");
        application.setScene(scene);
        application.show();

        //Matching 2560 x 1440 specifications
        application.setX(application.getX() - 35);
        application.setY(application.getY() - 39);
    }
}