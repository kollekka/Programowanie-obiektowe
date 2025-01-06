package calendar;

import org.hibernate.Session;

import javax.transaction.SystemException;
import org.hibernate.Transaction;
import java.time.LocalDate;

public class EventService {

    public void addEvent(String title, String description, LocalDate startTime,
                         LocalDate endTime) throws SystemException { // dodajemy usera jako argument
        Transaction transaction = null;
        Session session = null;
        User loggedInUser = UserSession.getInstance(null).getLoggedInUser();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = (Transaction) session.beginTransaction();

            Event event = new Event(title, description, startTime, endTime, loggedInUser); // przekazujemy usera do eventu
            session.save(event);

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

}