package teutonic.gestionprotocolos;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AnalysisCardController {

    @FXML
    private Label nombreLabel;

    @FXML
    private Label determinacionesLabel;

    @FXML
    private Label muestraLabel;

    public void setData(
            String nombre,
            String determinaciones,
            int numeroMuestra
    ) {
        nombreLabel.setText(nombre);
        determinacionesLabel.setText(determinaciones);
        muestraLabel.setText("Próx. muestra N° " + numeroMuestra);
    }
}