package teutonic.gestionprotocolos;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class ApplicationSmokeTest {

    private Stage stage;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        new HelloApplication().start(stage);
    }

    @Test
    void mainWindowOpens() {
        assertThat(stage.isShowing()).isTrue();
        assertThat(stage.getTitle()).isEqualTo("Hello!");
    }
}