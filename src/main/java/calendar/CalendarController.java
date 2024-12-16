package calendar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static calendar.Main.switchScene;

public class CalendarController {
    @FXML
    private Label monthYearLabel;
    @FXML
    private Button prevMonthButton;
    @FXML
    private Button nextMonthButton;
    @FXML
    private GridPane calendarGrid;

    private YearMonth currentYearMonth;

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        updateCalendar();
    }

    @FXML
    private void goToPreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void goToNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    private void updateCalendar() {
        monthYearLabel.setText(currentYearMonth.getMonth() + " " + currentYearMonth.getYear());
        calendarGrid.getChildren().clear();

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; // Adjust for GridPane starting index

        List<Label> dayLabels = new ArrayList<>();
        for (int i = 0; i < currentYearMonth.lengthOfMonth(); i++) {
            Label dayLabel = new Label(String.valueOf(i + 1));
            dayLabels.add(dayLabel);
        }

        int column = dayOfWeek;
        int row = 0;

        for (Label dayLabel : dayLabels) {
            calendarGrid.add(dayLabel, column, row);
            column++;
            if (column == 7) {
                column = 0;
                row++;
            }
        }
    }

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
}