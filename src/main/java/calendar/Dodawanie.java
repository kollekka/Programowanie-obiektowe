package calendar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.transaction.SystemException;
import java.sql.Timestamp;

public class Dodawanie {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker startTimePicker;
    @FXML private DatePicker endTimePicker;

    private final EventService eventDao = new EventService(); // zakładamy, że EventDao jest Twoją klasą dao do komunikacji z bazą danych

    @FXML
    protected void saveEvent(ActionEvent event) throws SystemException {
        // Ustaw wartości z pol formularza
        // Zapisz zdarzenie
        eventDao.addEvent(titleField.getText(),descriptionArea.getText(),startTimePicker.getValue().atStartOfDay().toLocalDate(),endTimePicker.getValue().atStartOfDay().toLocalDate());
    }
}

