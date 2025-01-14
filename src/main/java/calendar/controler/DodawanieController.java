package calendar.controler;

import calendar.service.EventService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.transaction.SystemException;

import static calendar.Main.switchScene;

public class DodawanieController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker startTimePicker;
    @FXML private DatePicker endTimePicker;
    @FXML private Label errorLabel;

    private final EventService eventDao = new EventService(); // zakładamy, że EventDao jest Twoją klasą dao do komunikacji z bazą danych

    @FXML
    protected void saveEvent(ActionEvent event) throws SystemException {
        // Ustaw wartości z pol formularza
        // Zapisz zdarzenie
        eventDao.addEvent(titleField.getText(),descriptionArea.getText(),startTimePicker.getValue().atStartOfDay().toLocalDate(),endTimePicker.getValue().atStartOfDay().toLocalDate(),errorLabel);
    }
    @FXML
    private void switchToSecondPage() {
        try {
            switchScene("/calendar/calendar-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

