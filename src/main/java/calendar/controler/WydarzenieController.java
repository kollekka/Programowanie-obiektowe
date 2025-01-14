package calendar.controler;
import calendar.config.HibernateUtil;
import calendar.model.Event;
import calendar.model.EventParticipant;
import calendar.model.User;
import calendar.service.EventData;
import calendar.service.EventService;
import calendar.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.hibernate.Session;

import javax.transaction.SystemException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static calendar.Main.switchScene;

public class WydarzenieController implements Initializable {

    @FXML
    public TableColumn <EventParticipant, String> userStatusColumn;
    @FXML
    private TableView<EventParticipant> tableView;

    @FXML
    private TableColumn<EventParticipant, String> userNameColumn;

    @FXML
    private TableColumn<EventParticipant, String> userMailColumn;

    @FXML
    private javafx.scene.control.TextField UserEmail;
    @FXML
    private Label errorLabel;

    private final EventService eventService = new EventService();

    int eventId = EventData.getInstance().getEventId();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Powiązanie kolumny z nazwą użytkownika (pole User.username w EventParticipant.user)
        userNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getParticipant().getUserName()));

        // Powiązanie kolumny z e-mailem użytkownika (pole User.email w EventParticipant.user)
        userMailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getParticipant().getEmail()));

        // Powiązanie kolumny ze statusem uczestnika (pole status w EventParticipant)
        userStatusColumn.setCellValueFactory(data ->
             new SimpleStringProperty(data.getValue().getStatus()));

        // Wypełnij tabelę uczestnikami wydarzenia
        showParticipants(eventId);
    }

    public void showParticipants(long eventId) {
        // Pobieranie uczestników na podstawie relacji Event -> EventParticipant
        List<EventParticipant> participants = eventService.getEventParticipants1((int) eventId);

        // Konwersja listy `EventParticipant` na ObservableList
        ObservableList<EventParticipant> participantObservableList = FXCollections.observableArrayList(participants);

        // Ustawienie danych w tabeli
        tableView.setItems(participantObservableList);
    }
    public void addUserToEvent() {
        String userEmail = this.UserEmail.getText();
        int eventId = this.eventId;
        org.hibernate.Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            UserService userService = new UserService();
            User userToAdd = userService.findUserByEmail(userEmail);

            if (userToAdd == null) {
                errorLabel.setText("Użytkownik o podanym adresie e-mail nie istnieje.");
                return;
            }

            EventParticipant eventParticipant = new EventParticipant();
            // Assuming that you have appropriate setters in your EventParticipant class
            eventParticipant.setEvent(eventService.getEventById(eventId));
            eventParticipant.setParticipant(userToAdd);
            eventParticipant.setStatus("pending");

            session.save(eventParticipant);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    public void deleteEvent() {
        int eventId = this.eventId; // Pobranie identyfikatora wydarzenia.
        Session session = null;
        org.hibernate.Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Pobierz wydarzenie, które chcesz usunąć
            Event eventToDelete = eventService.getEventById(eventId);
            if (eventToDelete == null) {
                showAlert("Error", "Event z ID " + eventId + " nie istnieje.");
                return;
            }

            // Usunięcie wydarzenia
            session.delete(eventToDelete);
            transaction.commit();

            showAlert("Success", "Event o id " + eventId + " został usunięty");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Wycofanie transakcji w przypadku błędu
            }
            e.printStackTrace();
            showAlert("Error", "Failed to delete the event: " + e.getMessage());
        } finally {
            if (session != null) {
                session.close(); // Zamknięcie sesji Hibernate
            }
        }
    }

    // Pomocnicza metoda do wyświetlania powiadomień (jeśli jeszcze nie dodano)
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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
