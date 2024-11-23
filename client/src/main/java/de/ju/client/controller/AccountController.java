package de.ju.client.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class AccountController {
    @FXML
    private ImageView profileImage;
    @FXML
    private Text welcomeText;
    @FXML
    private MFXListView<String> accountListView;
    @FXML
    private MFXButton addAccountButton;
    @FXML
    private MFXButton logoutButton;

    @FXML
    public void initialize() {
        accountListView.setDepthLevel(DepthLevel.LEVEL0);
        accountListView.getItems().addAll("user1@hotfemail.com", "user2@hotfemail.com", "user3@hotfemail.com", "user4@hotfemail.com", "user5@hotfemail.com");
        welcomeText.setText("jahid.uddin@hotfemail.com");
    }
}
