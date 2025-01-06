package calendar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static calendar.Main.switchScene;

public class Rejestracja {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // Stworzyć instancję UserService
    private UserService userService = new UserService();

    @FXML
    private void switchToSecondPage() {
        try {
            switchScene("/calendar/Logowanie.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleRegisterButtonAction(ActionEvent event) {
        String inputUsername = usernameField.getText();
        String inputPassword = passwordField.getText();
        String inputEmail = emailField.getText();

        if (!inputEmail.contains("@")) {
            errorLabel.setText("Nieprawidłowy adres e-mail!");
            return;
        }

        if (inputUsername.isEmpty() || inputPassword.isEmpty() || inputEmail.isEmpty()) {
            errorLabel.setText("Wszystkie pola są wymagane!");
            return;
        }

        if (!inputPassword.matches("(?=.*[A-Z]).{8,}")) {
            errorLabel.setText("Hasło musi zawierać conajmniej 8 znaków oraz jedną duzą literę!");
            return;
        }

        boolean userExists = userService.checkUserExists(inputUsername, inputEmail);

        if (userExists) {
            // Wyswietl komunikat o błędzie
            errorLabel.setText("Użytkownik o podanym nazwie użytkownika lub adresie email już istnieje!");
        } else {

            // Dodaj i zapisz nowego użytkownika do bazy danych
            userService.addUser(inputUsername, inputEmail, inputPassword);
            switchToSecondPage();
        }
    }
}
