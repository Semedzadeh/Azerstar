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
    exports com.azerstar.controller.goodsReceipt;
    opens com.azerstar.controller.goodsReceipt to javafx.fxml;
    exports com.azerstar.controller.analysis;
    opens com.azerstar.controller.analysis to javafx.fxml;
    exports com.azerstar.controller.drying;
    opens com.azerstar.controller.drying to javafx.fxml;
    exports com.azerstar.controller.warehouse;
    opens com.azerstar.controller.warehouse to javafx.fxml;
    exports com.azerstar.controller.report;
    opens com.azerstar.controller.report to javafx.fxml;
    exports com.azerstar.controller.settings;
    opens com.azerstar.controller.settings to javafx.fxml;

}

