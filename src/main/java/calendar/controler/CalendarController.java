package calendar.controler;

import calendar.config.HibernateUtil;
import calendar.service.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static calendar.Main.switchScene;

public class CalendarController {


    @FXML
    private Button switchButton;

    @FXML
    private void switchToSecondPage() {
        try {
            switchScene("/calendar/Logowanie.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button switchButton2;

    @FXML
    private void switchToSecondPage4() {
            try {
                switchScene("/calendar/Dodawanie.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
     }
    @FXML
    private void switchToSecondPage2() {
        try {
            switchScene("/calendar/Moje-wydarzenia.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void switchToSecondPage3() {
        try {
            switchScene("/calendar/Zaproszenia.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}