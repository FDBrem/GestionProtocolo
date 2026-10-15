package teutonic.gestionprotocolos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.flywaydb.core.Flyway;
import teutonic.gestionprotocolos.config.DatabaseConfig;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void init() throws Exception {
        DatabaseConfig.initialize();
        DatabaseConfig.verifyConfiguration();

        Flyway.configure()
                .dataSource(DatabaseConfig.getDatabaseUrl(), null, null)
                .locations("filesystem:src/main/resources/db/migration")
                .load()
                .migrate();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
