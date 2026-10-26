package teutonic.gestionprotocolos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class MainController {

    @FXML
    private GridPane cardsGrid;

    private AnalysisCardController tarjetaSeleccionada;

    @FXML
    public void initialize() {

        agregarTarjeta(
                "Partes de Trozado",
                "Enterobacterias · Staphylococcus · Aerobios",
                648,
                0,
                0
        );

        agregarTarjeta(
                "Hamburguesas",
                "Salmonella · E. coli",
                652,
                1,
                0
        );

        agregarTarjeta(
                "Harinas",
                "Mohos · Levaduras",
                659,
                2,
                0
        );

        agregarTarjeta(
                "Lácteos",
                "Coliformes · Salmonella",
                664,
                0,
                1
        );

        agregarTarjeta(
                "Carnes",
                "E. coli · Salmonella",
                671,
                1,
                1
        );
    }

    private void agregarTarjeta(
            String nombre,
            String determinaciones,
            int numeroMuestra,
            int columna,
            int fila
    ) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource("analysis-card.fxml")
            );
            Node card = loader.load();

            AnalysisCardController controller =
                    loader.getController();

            controller.setData(
                    nombre,
                    determinaciones,
                    numeroMuestra
            );

            controller.setOnClick(() -> {

                if (tarjetaSeleccionada != null) {
                    tarjetaSeleccionada.setSelected(false);
                }

                controller.setSelected(true);

                tarjetaSeleccionada = controller;

                System.out.println("Click en: " + nombre);
            });

            cardsGrid.add(card, columna, fila);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}