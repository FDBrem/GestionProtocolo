module teutonic.gestionprotocolos {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires flyway.core;
    requires java.sql;
    requires org.jdbi.v3.core;
    requires org.jdbi.v3.sqlobject;
    requires org.slf4j;
    requires org.mapstruct;

    opens teutonic.gestionprotocolos.repository to org.jdbi.v3.core;
    opens teutonic.gestionprotocolos.persistence.records to org.jdbi.v3.core;
    opens db.migration to flyway.core;
    opens teutonic.gestionprotocolos to javafx.fxml;
    exports teutonic.gestionprotocolos;
    opens teutonic.gestionprotocolos.mapper to org.jdbi.v3.core, org.mapstruct;
}