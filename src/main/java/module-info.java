module com.azerstar {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.azerstar to javafx.fxml;
    exports com.azerstar;
    exports com.azerstar.Controller;
    exports com.azerstar.Model;
    exports com.azerstar.Service;

}
//153