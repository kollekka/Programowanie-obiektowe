package calendar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/calendar/calendar-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Kalendarz");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
    public static void switchScene(String fxmlFile) throws Exception {
        Parent root = FXMLLoader.load(requireNonNull(Main.class.getResource(fxmlFile)));
        primaryStage.getScene().setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}