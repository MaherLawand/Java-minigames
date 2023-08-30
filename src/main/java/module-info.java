module com.project.introtohumancomputerinteraction {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                        requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;
    requires javafx.swing;

    opens com.project.introtohumancomputerinteraction to javafx.fxml;
    exports com.project.introtohumancomputerinteraction;
}