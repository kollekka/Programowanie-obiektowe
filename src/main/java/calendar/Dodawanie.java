package calendar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static calendar.Main.switchScene;

public class Dodawanie {

    private Button switchButton2;

    @FXML
    private void switchToSecondPage3() {
        try {
            switchScene("/calendar/calendar-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}