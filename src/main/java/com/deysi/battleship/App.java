package com.deysi.battleship;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX launcher for the Battleship game.
 */
public class App extends Application {

    /**
     * Creates JavaFX application instance.
     */
    public App() {
        // Required public constructor for JavaFX launcher.
    }

    /**
     * Builds and shows the main JavaFX stage.
     *
     * @param stage primary stage provided by JavaFX
     * @throws IOException when FXML or styles cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/deysi/battleship/view/battle-naval.fxml"));
        Scene scene = new Scene(loader.load(), 1240, 770);
        scene.getStylesheets().add(App.class.getResource("/com/deysi/battleship/view/battle-naval.css").toExternalForm());
        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        stage.setMinWidth(1120);
        stage.setMinHeight(700);
        stage.show();
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
