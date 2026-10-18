module teutonic.gestionprotocolos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires atlantafx.base;

    opens teutonic.gestionprotocolos to javafx.fxml;
    exports teutonic.gestionprotocolos;
}