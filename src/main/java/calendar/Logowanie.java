package calendar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static calendar.Main.switchScene;

public class Logowanie {

    private Button switchButton2;

    @FXML
    private void switchToSecondPage2() {
        try {
            switchScene("/calendar/calendar-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
