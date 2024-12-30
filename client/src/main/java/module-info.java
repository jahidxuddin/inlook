module de.ju.client {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires MaterialFX;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens de.ju.client to javafx.fxml;
    exports de.ju.client;
    exports de.ju.client.service.email;
    opens de.ju.client.service.email to javafx.fxml;
    exports de.ju.client.component;
    opens de.ju.client.component to javafx.fxml;
    exports de.ju.client.dashboard.controller;
    opens de.ju.client.dashboard.controller to javafx.fxml;
    exports de.ju.client.dashboard.component;
    opens de.ju.client.dashboard.component to javafx.fxml;
    exports de.ju.client.account.controller;
    opens de.ju.client.account.controller to javafx.fxml;
}