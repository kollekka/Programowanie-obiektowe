package calendar;

import java.time.LocalDate;

public class SelectedDate {
    private static LocalDate date;

    public static void setDate(LocalDate date) {
        SelectedDate.date = date;
    }

    public static LocalDate getDate() {
        return date;
    }
}
