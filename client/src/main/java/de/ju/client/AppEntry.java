package de.ju.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AppEntry extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        showAccountView(primaryStage);
    }

    private void showAccountView(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/view/AccountView.fxml"));
        Parent modalRoot = loader.load();
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(primaryStage); // Blocks interaction with primary stage
        modalStage.setScene(new Scene(modalRoot, 400, 500));
        modalStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/style.css")).toExternalForm());
        modalStage.setTitle("Konto auswählen");
        modalStage.setResizable(false);
        modalStage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
