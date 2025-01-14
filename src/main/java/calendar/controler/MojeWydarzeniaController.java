package calendar.controler;

import calendar.config.HibernateUtil;
import calendar.model.Event;
import calendar.model.EventParticipant;
import calendar.model.User;
import calendar.service.EventData;
import calendar.service.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import javax.transaction.SystemException;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static calendar.Main.switchScene;

public class MojeWydarzeniaController {

    @FXML
    private GridPane calendarGrid;

    private final SessionFactory factory = HibernateUtil.getSessionFactory();

    @FXML
    private Label headerLabel;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button CofnijButton;

    private LocalDate currentDate;

    Label[] dayLabels = new Label[7];

    User loggedInUser = UserSession.getLoggedInUser();

    public void initialize() throws SystemException {

        dayLabels = new Label[7];
        for (int i = 0; i < dayLabels.length; i++) {
            dayLabels[i] = new Label();
            GridPane.setConstraints(dayLabels[i], i, 0); // ustawienie etykiety w odpowiedniej kolumnie i wierszu 2
            calendarGrid.getChildren().add(dayLabels[i]);
        }
        // Ustalenie aktualnej daty
        currentDate = LocalDate.now();

        updateWeekView();

        nextButton.setOnAction(event -> {
            currentDate = currentDate.plusWeeks(1);
            try {
                updateWeekView();
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        });

        prevButton.setOnAction(event -> {
            currentDate = currentDate.minusWeeks(1);
            try {
                updateWeekView();
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void updateWeekView() throws SystemException {
        // Pobieranie wydarzeń z bazy
        List<Event> eventList = loadEventsFromDatabase();

        // Czyszczenie kalendarza z poprzednich wydarzeń
        if (calendarGrid.getChildren().size() > 7) {
            calendarGrid.getChildren().subList(7, calendarGrid.getChildren().size()).clear();
        }

        // Aktualizacja nagłówka na aktualny miesiąc
        DateFormatSymbols dfs = new DateFormatSymbols();
        String month = dfs.getMonths()[currentDate.getMonthValue()-1];
        headerLabel.setText(month + " " + currentDate.getYear());

        // Aktualizacja widoku
        updateCalendarWithEvents(eventList);

        LocalDate startDateOfWeek = currentDate.with(DayOfWeek.MONDAY);
        for (int i = 0; i < dayLabels.length; i++) {
            String dayOfMonth = Integer.toString(startDateOfWeek.plusDays(i).getDayOfMonth());
            dayLabels[i].setText(dayOfMonth);
        }
    }

    private List<Event> loadEventsFromDatabase() {
        List<Event> eventList = null; // Lista wydarzeń
        org.hibernate.Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Logika pobierania danych
            LocalDate startDateOfWeek = currentDate.with(DayOfWeek.MONDAY);
            LocalDate endDateOfWeek = currentDate.with(DayOfWeek.SUNDAY);

            if (loggedInUser == null) {
                throw new RuntimeException("Zalogowany użytkownik jest pusty.");
            }

            Query<EventParticipant> query = session.createQuery(
                    "SELECT ep FROM EventParticipant ep " +
                            "JOIN FETCH ep.event e " +
                            "WHERE ep.participant = :user " +
                            "AND e.startTime BETWEEN :startDate AND :endDate",
                    EventParticipant.class
            );
            query.setParameter("user", loggedInUser);
            query.setParameter("startDate", startDateOfWeek);
            query.setParameter("endDate", endDateOfWeek);

            List<EventParticipant> participants = query.getResultList();

            eventList = participants.stream()
                    .map(EventParticipant::getEvent)
                    .distinct()
                    .collect(Collectors.toList());

            transaction.commit();
        } catch (Exception e) {
            // Rollback tylko, gdy transakcja została rozpoczęta
            if (transaction != null && transaction.getStatus().canRollback()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException rollbackException) {
                    e.addSuppressed(rollbackException); // Dodanie rollbackException do oryginalnego wyjątku
                }
            }
            throw new RuntimeException("Nie udało się załadować wydarzeń użytkownika z bazy danych", e);
        }

        return eventList; // Zwrot listy wydarzeń
    }
    private void updateCalendarWithEvents(List<Event> events) {
        // Sortujemy wydarzenia po godzinach startu
        events.sort(Comparator.comparing(Event::getStartTime));

        // Usuwamy wszystko od trzeciego rzędu (wydarzenia) w górę
        if (calendarGrid.getChildren().size() > 14) {
           calendarGrid.getChildren().remove(14, calendarGrid.getChildren().size());
       }

        int maxRowsUsed = 1;

        for (int i = 0; i < 7; i++) {
            int finalI = i;
            List<Event> eventsForDay = events.stream()
                    .filter(e -> e.getStartTime().getDayOfWeek().getValue() == finalI + 1)
                    .collect(Collectors.toList());

            int numberOfRowsForDay = 1; // Zacznij od 1, ponieważ pierwszy rzędy to nagłówek.
            for (Event event : eventsForDay) {
                Button eventButton = new Button(event.getTitle());
                eventButton.setStyle("-fx-background-color: #336699; -fx-text-fill: white;");
                eventButton.setOnAction(actionEvent -> {
                    try {
                        // Zapisz eventId w Singletonie przed przeładowaniem sceny.
                        EventData.getInstance().setEventId(event.getId());

                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/calendar/wydarzenie.fxml")));
                        Scene scene = new Scene(root);
                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                GridPane.setConstraints(eventButton, finalI, numberOfRowsForDay);
                calendarGrid.getChildren().add(eventButton);
                numberOfRowsForDay++;
            }

            if (numberOfRowsForDay > maxRowsUsed) {
                maxRowsUsed = numberOfRowsForDay;
            }
        }

        // Dodajemy puste rzędy do GridPane (jeżeli są potrzebne)
        for (int i = calendarGrid.getRowConstraints().size(); i < maxRowsUsed; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            calendarGrid.getRowConstraints().add(rowConstraints);
        }
    }
    @FXML
    private void switchToSecondPage() {
        try {
            switchScene("calendar-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


