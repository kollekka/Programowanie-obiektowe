package calendar;

import calendar.config.DatabaseTest;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static java.util.Objects.requireNonNull;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/calendar/Logowanie.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Kalendarz");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    public static void switchScene(String fxmlFile) throws Exception {
        Parent root = FXMLLoader.load(requireNonNull(Main.class.getResource(fxmlFile)));
        primaryStage.getScene().setRoot(root);
    }
    public static void main(String[] args) {
        launch(args);
        String url = "jdbc:postgresql://localhost:5433/Calendar";
        String username = "postgres";
        String password = "student";

        boolean isConnected = DatabaseTest.testConnection(url, username, password);
        System.out.println("Connection status: " + isConnected);

    }

}