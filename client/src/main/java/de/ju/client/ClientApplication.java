package de.ju.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("E-Mail Client");
        primaryStage.setScene(new Scene(root, 1100, 750));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
