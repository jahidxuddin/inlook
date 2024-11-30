package de.ju.client.controller.home;

import de.ju.client.model.Email;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class EmailListCell extends MFXListCell<Email> {
    public EmailListCell(MFXListView<Email> listView, Email item) {
        super(listView, item);

        Label senderLabel = new Label(item.getSender().substring(0, item.getSender().indexOf("@")));
        senderLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        Label subjectLabel = new Label(item.getSubject());
        subjectLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label timestampLabel = new Label(item.getDate());
        timestampLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        HBox cellContainer = new HBox(10);
        cellContainer.setAlignment(Pos.CENTER_LEFT);
        cellContainer.getChildren().addAll(senderLabel, subjectLabel, timestampLabel);
        cellContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
        cellContainer.setMinWidth(900);

        getChildren().addAll(cellContainer);
    }
}
