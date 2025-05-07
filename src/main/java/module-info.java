module com.azerstar {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires de.jensd.fx.glyphs.fontawesome;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jdk.compiler;
    requires java.desktop;

    opens com.azerstar.controller to javafx.fxml;
    opens com.azerstar to javafx.fxml;
    exports com.azerstar;
    exports com.azerstar.controller;
    exports com.azerstar.model;
    exports com.azerstar.service;

}

