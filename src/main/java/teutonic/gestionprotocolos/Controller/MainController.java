package teutonic.gestionprotocolos.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;

public class MainController {

    // =========================================================
    // GRIDS DE FORMULARIOS
    // =========================================================

    @FXML
    private GridPane productCardsGrid;

    @FXML
    private GridPane flourCardsGrid;

    @FXML
    private GridPane environmentalCardsGrid;


    // =========================================================
    // PANEL DERECHO
    // =========================================================

    @FXML
    private Label selectedFormTitle;

    @FXML
    private Label formDate;

    @FXML
    private Label formMethod;

    @FXML
    private Label formKit;

    @FXML
    private Label formWeight;

    @FXML
    private Label formSamplingPoint;

    @FXML
    private Label formProtocol;

    @FXML
    private Label formNextSample;


    // =========================================================
    // ESTADO
    // =========================================================

    private AnalysisCardController tarjetaSeleccionada;


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @FXML
    public void initialize() {

        cargarMuestrasDeProducto();
        cargarHarinas();
        cargarMonitoreoAmbiental();

        // Selección inicial
        FormularioDemo formularioInicial = formulariosProducto().get(3);

        actualizarPanel(formularioInicial);
    }


    // =========================================================
    // MUESTRAS DE PRODUCTO
    // =========================================================

    private void cargarMuestrasDeProducto() {

        List<FormularioDemo> formularios = formulariosProducto();

        for (int i = 0; i < formularios.size(); i++) {

            FormularioDemo formulario = formularios.get(i);

            agregarTarjeta(
                    formulario,
                    productCardsGrid,
                    i % 3,
                    i / 3
            );
        }
    }


    // =========================================================
    // HARINAS
    // =========================================================

    private void cargarHarinas() {

        List<FormularioDemo> formularios = List.of(

                new FormularioDemo(
                        "Harina de vísceras",
                        "Salmonella spp · por bolsón",
                        609,
                        "Salmonella spp",
                        "Placa selectiva",
                        "25 g",
                        "Recepción",
                        "Sin N° de protocolo"
                ),

                new FormularioDemo(
                        "Harina de plumas y sangre",
                        "Salmonella spp · por bolsón",
                        595,
                        "Salmonella spp",
                        "Placa selectiva",
                        "25 g",
                        "Recepción",
                        "Sin N° de protocolo"
                )
        );

        for (int i = 0; i < formularios.size(); i++) {

            agregarTarjeta(
                    formularios.get(i),
                    flourCardsGrid,
                    i % 3,
                    i / 3
            );
        }
    }


    // =========================================================
    // MONITOREO AMBIENTAL
    // =========================================================

    private void cargarMonitoreoAmbiental() {

        List<FormularioDemo> formularios = List.of(

                new FormularioDemo(
                        "Aerobios ambiente",
                        "Recuento por contacto (UFC/cm²)",
                        0,
                        "Aerobios",
                        "Placa de contacto",
                        "-",
                        "Ambiente",
                        "Planilla por fecha"
                ),

                new FormularioDemo(
                        "Listeria",
                        "Hisopos · PCR · resultado cualitativo",
                        0,
                        "Listeria",
                        "PCR",
                        "-",
                        "Ambiente",
                        "Planilla por fecha"
                ),

                new FormularioDemo(
                        "Mohos y levaduras",
                        "Hisopado de superficies (UFC/cm²)",
                        0,
                        "Mohos y levaduras",
                        "Placa de contacto",
                        "-",
                        "Superficie",
                        "Planilla por fecha"
                )
        );

        for (int i = 0; i < formularios.size(); i++) {

            agregarTarjeta(
                    formularios.get(i),
                    environmentalCardsGrid,
                    i % 3,
                    i / 3
            );
        }
    }


    // =========================================================
    // DATOS DE PRODUCTOS
    // =========================================================

    private List<FormularioDemo> formulariosProducto() {

        return List.of(

                new FormularioDemo(
                        "Pollo entero (hisopado)",
                        "Coliformes · E. coli",
                        69,
                        "Coliformes / E. coli",
                        "Hisopado",
                        "-",
                        "Pollo entero",
                        "Sin N° de protocolo"
                ),

                new FormularioDemo(
                        "CMS",
                        "Aerobios · E. coli · Salmonella · Huesos",
                        708,
                        "Aerobios / E. coli / Salmonella",
                        "Malaxado",
                        "25 g",
                        "CMS",
                        "Sin N° de protocolo"
                ),

                new FormularioDemo(
                        "Suprema",
                        "Aerobios · E. coli · Salmonella",
                        6,
                        "Aerobios / E. coli / Salmonella",
                        "Malaxado",
                        "25 g",
                        "Suprema",
                        "Sin N° de protocolo"
                ),

                new FormularioDemo(
                        "Partes de Trozado",
                        "Enterobacterias · Staphylococcus · Aerobios",
                        648,
                        "Malaxado en bolsa estéril",
                        "Placas Petrifilm 3M",
                        "22,5 g",
                        "Trozado",
                        "Sin N° de protocolo"
                ),

                new FormularioDemo(
                        "Menudos de aves",
                        "Enterobacterias · Staphylococcus · Aerobios",
                        685,
                        "Malaxado en bolsa estéril",
                        "Placas Petrifilm 3M",
                        "22,5 g",
                        "Menudos",
                        "Sin N° de protocolo"
                ),

                new FormularioDemo(
                        "Garras y Tronquitos",
                        "Enterobacterias · Staphylococcus · Aerobios",
                        685,
                        "Malaxado en bolsa estéril",
                        "Placas Petrifilm 3M",
                        "22,5 g",
                        "Garras y Tronquitos",
                        "Sin N° de protocolo"
                ),

                new FormularioDemo(
                        "Piel de cogote",
                        "Salmonella spp",
                        58,
                        "Salmonella spp",
                        "Hisopado",
                        "-",
                        "Piel de cogote",
                        "Sin N° de protocolo"
                )
        );
    }


    // =========================================================
    // CREACIÓN DE TARJETA
    // =========================================================

    private void agregarTarjeta(
            FormularioDemo formulario,
            GridPane grid,
            int columna,
            int fila
    ) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainController.class.getResource(
                            "/teutonic/gestionprotocolos/analysis-card.fxml"
                    )
            );

            Node card = loader.load();

            AnalysisCardController controller =
                    loader.getController();

            controller.setData(
                    formulario.nombre(),
                    formulario.determinaciones(),
                    formulario.numeroMuestra()
            );

            controller.setOnClick(() -> {

                if (tarjetaSeleccionada != null) {
                    tarjetaSeleccionada.setSelected(false);
                }

                controller.setSelected(true);

                tarjetaSeleccionada = controller;

                actualizarPanel(formulario);
            });

            grid.add(card, columna, fila);

            // Selección inicial
            if (formulario.nombre().equals("Partes de Trozado")) {

                controller.setSelected(true);
                tarjetaSeleccionada = controller;
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo cargar la tarjeta: "
                            + formulario.nombre(),
                    e
            );
        }
    }


    // =========================================================
    // ACTUALIZACIÓN DEL PANEL DERECHO
    // =========================================================

    private void actualizarPanel(FormularioDemo formulario) {

        selectedFormTitle.setText(formulario.nombre());

        formDate.setText("01/11/2026 (hoy)");

        formMethod.setText(formulario.metodo());

        formKit.setText(formulario.kit());

        formWeight.setText(formulario.peso());

        formSamplingPoint.setText(formulario.puntoToma());

        formProtocol.setText(formulario.protocolo());

        if (formulario.numeroMuestra() > 0) {
            formNextSample.setText(
                    String.valueOf(formulario.numeroMuestra())
            );
        } else {
            formNextSample.setText("-");
        }
    }


    // =========================================================
    // MODELO TEMPORAL DE UI
    // =========================================================

    private record FormularioDemo(
            String nombre,
            String determinaciones,
            int numeroMuestra,
            String metodo,
            String kit,
            String peso,
            String puntoToma,
            String protocolo
    ) {
    }
}