package calendar.controler;

import calendar.config.HibernateUtil;
import calendar.model.Event;
import calendar.model.EventParticipant;
import calendar.model.User;
import calendar.service.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static calendar.Main.switchScene;

public class ZaproszeniaController implements Initializable {
    @FXML
    private TableView<Event> eventTableView;
    @FXML
    private TableColumn<Event, String> eventNameColumn;
    @FXML
    private TableColumn<Event, String> eventDateColumn;
    @FXML
    private TableColumn<Event, Void> actionColumn;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actionColumn.setCellFactory(param -> new TableCell<Event, Void>() {
            final Button acceptButton = new Button("Akceptuj");
            final Button rejectButton = new Button("Odrzuć");

            // Tworzymy panel, który pomieści przyciski
            HBox pane = new HBox(acceptButton, rejectButton);

            public void updateItem(Void item, boolean empty) {   // Zmienione z Event na Void
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Event currentEvent = getTableView().getItems().get(getIndex()); // Pobiera aktualne wydarzenie z TableView
                    acceptButton.setOnAction(e -> acceptEvent(currentEvent));
                    rejectButton.setOnAction(e -> rejectEvent(currentEvent));
                    setGraphic(pane);
                }
            }
        });

        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("title")); // użyj nazwy właściwości z klasy Event
        eventDateColumn.setCellValueFactory(data -> {
            LocalDate date = data.getValue().getStartTime(); // LocalDate pozostaje
            String strDate = date.format(DATE_FORMATTER); // Formatowanie bez godzin
            return new SimpleStringProperty(strDate);
        });

        // Pobierz aktualny użytkownik, dla którego chcemy wyświetlić eventy
        User currentUser = UserSession.getInstance(null).getLoggedInUser();

        // Wywołaj naszą metodę getAllPendingEventsForUser i przekaż tą listę do widoku
        List<Event> pendingEvents = getAllPendingEventsForUser(currentUser);

        // Konwertujemy naszą listę na ObservableList i ustawiamy ją jako źródło danych dla tabeli.
        ObservableList<Event> eventObservableList = FXCollections.observableArrayList(pendingEvents);
        eventTableView.setItems(eventObservableList);


    }

    public List<Event> getAllPendingEventsForUser(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<EventParticipant> cq = cb.createQuery(EventParticipant.class);
        Root<EventParticipant> root = cq.from(EventParticipant.class);

        // tworzenie warunków zapytania
        cq.select(root).where(cb.and(
                cb.equal(root.get("participant"), user),
                cb.equal(root.get("status"), "pending")
        ));

        List<EventParticipant> eventParticipants = session.createQuery(cq).getResultList();

        // zamiana EventParticipant na Event i zwrócenie rezultatu
        return eventParticipants.stream().map(EventParticipant::getEvent).collect(Collectors.toList());
    }

    private void acceptEvent(Event event) {
        System.out.println("Accept event called with " + event);
        User currentUser = UserSession.getInstance(null).getLoggedInUser();

        // otwieramy nową sesję
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // znajdujemy odpowiedni EventParticipant
        EventParticipant eventParticipant = findEventParticipantForEventAndUser(session, event, currentUser);
        if (eventParticipant != null) {
            System.out.println("Found participant: " + eventParticipant);
            // zmieniamy status
            eventParticipant.setStatus("joined");
            // zapisujemy zmiany
            session.saveOrUpdate(eventParticipant);
            System.out.println("Saved participant: " + eventParticipant);
        } else {
            System.out.println("Couldn't find participant");
        }

        // zamknij transakcję i sesję
        session.getTransaction().commit();
        session.close();

        // Aktualizacja wyświetlanych danych
        reloadData();
    }

    private void rejectEvent(Event event) {
        System.out.println("Reject event called with " + event);
        // Pobierz aktualny użytkownik
        User currentUser = UserSession.getInstance(null).getLoggedInUser();

        // otwieramy nową sesję
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // znajdujemy odpowiedni EventParticipant
        EventParticipant eventParticipant = findEventParticipantForEventAndUser(session, event, currentUser);
        if (eventParticipant != null) {
            System.out.println("Found participant: " + eventParticipant);
            // usuwamy EventParticipant
            session.delete(eventParticipant);
            System.out.println("Deleted participant: " + eventParticipant);
        } else {
            System.out.println("Couldn't find participant");
        }

        // zamknij transakcję i sesję
        session.getTransaction().commit();
        session.close();

        // Aktualizacja wyświetlanych danych
        reloadData();
    }

    private EventParticipant findEventParticipantForEventAndUser(Session session, Event event, User user) {

        // Używamy CriteriaBuilder do stworzenia zapytania
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<EventParticipant> cq = cb.createQuery(EventParticipant.class);
        Root<EventParticipant> root = cq.from(EventParticipant.class);

        // tworzenie warunków zapytania
        cq.select(root).where(cb.and(
                cb.equal(root.get("event"), event),
                cb.equal(root.get("participant"), user),
                cb.equal(root.get("status"), "pending")
        ));

        // wywołujemy zapytanie
        List<EventParticipant> result = session.createQuery(cq).getResultList();

        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    private void reloadData() {
        // Pobierz aktualny użytkownik, dla którego chcemy wyświetlić eventy
        User currentUser = UserSession.getInstance(null).getLoggedInUser();

        // Wywołaj naszą metodę getAllPendingEventsForUser i przekaż tą listę do widoku
        List<Event> pendingEvents = getAllPendingEventsForUser(currentUser);

        // Konwertujemy naszą listę na ObservableList i ustawiamy ją jako źródło danych dla tabeli.
        ObservableList<Event> eventObservableList = FXCollections.observableArrayList(pendingEvents);
        eventTableView.setItems(eventObservableList);
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
