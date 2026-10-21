package teutonic.gestionprotocolos;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        // 1. Registrar el error técnico completo
        log.error(
                "Se produjo una excepción no controlada en el hilo: {}",
                thread.getName(),
                throwable
        );

        // 2. Mostrar el mensaje al usuario
        mostrarDialogoError();
    }

    private void mostrarDialogoError() {

        Runnable mostrar = () -> {

            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    "Se produjo un error inesperado.\n\n"
                            + "El detalle fue registrado en los logs.",
                    ButtonType.OK
            );

            alert.setTitle("Error");
            alert.setHeaderText("Ocurrió un problema");

            alert.showAndWait();
        };

        // Nos aseguramos de ejecutar la interfaz en el hilo de JavaFX
        if (Platform.isFxApplicationThread()) {
            mostrar.run();
        } else {
            Platform.runLater(mostrar);
        }
    }
}