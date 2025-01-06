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

        List<Button> dayButtons = new ArrayList<>();
        for (int i = 0; i < currentYearMonth.lengthOfMonth(); i++) {
            final int day = i + 1;
            // Create a new button instead of a label
            Button dayButton = new Button(String.valueOf(i + 1));

            // (Optional) Add an action to the button
            dayButton.setOnAction(event -> {
                try {
                    LocalDate selectedDate = currentYearMonth.atDay(day);
                    SelectedDate.setDate(selectedDate);
                    // Here you put the name of the .fxml file you want to switch to.
                    switchScene("/calendar/Dodawanie.fxml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            dayButtons.add(dayButton);
        }

        int column = dayOfWeek;
        int row = 0;

        for (Button dayButton : dayButtons) {
            calendarGrid.add(dayButton, column, row);
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