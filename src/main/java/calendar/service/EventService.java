package calendar.service;

import calendar.config.HibernateUtil;
import calendar.model.Event;
import calendar.model.EventParticipant;
import calendar.model.User;
import javafx.scene.control.Label;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.SystemException;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventService {

    public void addEvent(String title, String description, LocalDate startTime,
                         LocalDate endTime, Label errorLabel) throws SystemException {
        Transaction transaction = null;
        Session session = null;
        User loggedInUser = UserSession.getInstance(null).getLoggedInUser();

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Zapytanie HQL do policzenia wydarzeń w danym dniu
            String hql = "SELECT COUNT(e) FROM Event e WHERE e.startTime = :startTime AND e.user = :user";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("startTime", startTime);
            query.setParameter("user", loggedInUser);

            Long eventCount = query.getSingleResult();

            if (eventCount >= 3) {
                // Jeśli w danym dniu są już 3 wydarzenia -> wyświetl komunikat w errorLabel
                if (errorLabel != null) {
                    errorLabel.setText("Nie możesz dodać więcej niż 3 wydarzenia w tym dniu.");
                }
                transaction.rollback(); // Nie wykonujemy żadnych zmian
                return;
            }

            // Dodanie nowego wydarzenia
            Event event = new Event(title, description, startTime, endTime, loggedInUser);
            session.save(event);

            // Dodanie uczestnika (aktualny zalogowany użytkownik)
            EventParticipant eventParticipant = new EventParticipant();
            eventParticipant.setEvent(event);
            eventParticipant.setParticipant(loggedInUser);
            eventParticipant.setStatus("joined");

            session.persist(eventParticipant);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            if (errorLabel != null) {
                errorLabel.setText("Wystąpił błąd podczas dodawania wydarzenia.");
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    public List<User> getEventParticipants(int id) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<EventParticipant> cq = cb.createQuery(EventParticipant.class);
            Root<EventParticipant> root = cq.from(EventParticipant.class);

            // Here we add the condition for the query
            cq.where(cb.equal(root.get("event").get("id"), id));

            Query<EventParticipant> query = session.createQuery(cq);

            List<EventParticipant> participantsList = query.getResultList();

            // Map EventParticipant objects to User objects
            List<User> usersList = participantsList.stream()
                    .map(EventParticipant::getParticipant)
                    .collect(Collectors.toList());

            transaction.commit();

            return usersList;
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

        return new ArrayList<>(); // Return empty list if an error occurs
    }
    public Event getEventById(int id){
        // Creating a session and transaction objects
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            // Getting the specific record using Hibernate's get method
            Event event = session.get(Event.class, id);
            transaction.commit();
            return event;
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
        return null;
    }
    public List<EventParticipant> getEventParticipants1(int eventId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EventParticipant> participants = session.createQuery(
                        "FROM EventParticipant ep WHERE ep.event.id = :eventId", EventParticipant.class)
                .setParameter("eventId", eventId)
                .getResultList();
        session.close();
        return participants;
    }
}