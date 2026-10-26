package teutonic.gestionprotocolos;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.css.PseudoClass;

public class AnalysisCardController {

    @FXML
    private VBox card;

    @FXML
    private Label nombreLabel;

    @FXML
    private Label determinacionesLabel;

    @FXML
    private Label muestraLabel;

    private static final PseudoClass SELECTED_PSEUDO_CLASS =
            PseudoClass.getPseudoClass("selected");


    public void setData(
            String nombre,
            String determinaciones,
            int numeroMuestra
    ) {
        nombreLabel.setText(nombre);
        determinacionesLabel.setText(determinaciones);
        muestraLabel.setText(
                "Próx. muestra N° " + numeroMuestra
        );
    }

    public void setSelected(boolean selected) {

        card.pseudoClassStateChanged(
                SELECTED_PSEUDO_CLASS,
                selected
        );
    }

    public void setOnClick(Runnable accion) {
        card.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                accion.run();
            }
        });
    }
}