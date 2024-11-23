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

    opens de.ju.client to javafx.fxml;
    exports de.ju.client;
    exports de.ju.client.controller;
    opens de.ju.client.controller to javafx.fxml;
}