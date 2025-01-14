package calendar.service;

import calendar.config.HibernateUtil;
import calendar.model.User;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserService {
    private SessionBuilder<SessionBuilder> sessionFactory;

    public void addUser(String userName, String email, String passwordHash) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            User user = new User(userName, email, passwordHash);
            session.save(user);

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
    public User authenticate(String username, String password) {
        // Pobiera użytkownika z bazy danych za pomocą nazwy użytkownika
        User user = getUserByUsername(username);

        if (user != null) {
            // Sprawdza, czy hasło jest poprawne (zakłada, że hasło jest przechowywane jako zahaszowane)
            if (password.equals(user.getPasswordHash())) {
                // Użytkownik istnieje i hasło jest poprawne
                return user;
            }
        }

        // Użytkownik nie istnieje lub hasło jest niepoprawne
        return null;
    }
    public User getUserByUsername(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM User WHERE username = :username");
            ((Query<?>) query).setParameter("username", username);

            List<User> users = query.list();

            if (!users.isEmpty()) {
                return users.get(0); // Zwraca pierwszego użytkownika, który pasuje do nazwy użytkownika. Zakładamy, że nazwy użytkowników są unikalne.
            } else {
                return null; // Jeżeli brak użytkownika o podanej nazwie.
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return null; // Jeżeli nastąpił wyjątek.
    }
    public boolean checkUserExists(String username, String email) {
        Session session = null;
        boolean userExists = false;

        try {
            session = sessionFactory.openSession();
            Query query = session.createQuery("FROM User WHERE username = :username OR email = :email");
            query.setParameter("username", username);
            query.setParameter("email", email);
            User user = (User) query.uniqueResult();

            if (user != null)
                userExists = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null)
                session.close();
        }

        return userExists;
    }
    public User findUserByEmail(String email) {
        Session session = null;
        Transaction transaction = null;
        User user = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cr = cb.createQuery(User.class);
            Root<User> root = cr.from(User.class);

            // Here we add the condition for the query.
            cr.where(cb.equal(root.get("email"), email));

            Query<User> query = session.createQuery(cr);
            List<User> resultList = query.getResultList();

            if (!resultList.isEmpty()) {
                // Assume that email is unique and get the first result.
                user = resultList.get(0);
            }

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

        return user;
    }
}
