package calendar.controler;

import calendar.model.User;
import calendar.service.UserService;
import calendar.service.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.event.ActionEvent;

import static calendar.Main.switchScene;
import static calendar.service.UserSession.cleanUserSession;

public class LogowanieController {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button nextButton;

    @FXML
    private Label errorLabel;


    @FXML
    private void switchToSecondPage2() {
        try {
            switchScene("/calendar/calendar-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void switchToSecondPage() {
        try {
            switchScene("/calendar/Rejestracja.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        // Pobierz dane wprowadzone przez użytkownika
        String inputUsername = username.getText();
        String inputPassword = password.getText();

        UserService userService = new UserService();
        userService.authenticate(inputUsername,inputPassword );
        // Pobierz dane użytkownika o wprowadzonej nazwie z bazy danych
        User user = userService.getUserByUsername(inputUsername);

        if (user != null && user.getPasswordHash().equals(inputPassword)) {
            System.out.println("Zalogowano pomyślnie!");
            UserSession.getInstance(user);

            // Przejdź do kolejnego okna po udanym zalogowaniu
            switchToSecondPage2();
        } else {
            errorLabel.setText("Nieprawidłowe dane logowania!");
        }
        UserSession.getInstance(user);
    }
}


