package teutonic.gestionprotocolos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import atlantafx.base.theme.PrimerLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final Logger log =
            LoggerFactory.getLogger(HelloApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(
                new GlobalExceptionHandler()
        );

        log.info("Aplicación iniciada correctamente");

        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );

        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        HelloApplication.class.getResource("main-view.fxml")
                );

        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        stage.setTitle("Gestión de Protocolos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
