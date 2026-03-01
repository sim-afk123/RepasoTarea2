package com.tallerbici.viewController;

import com.tallerbici.model.Bicicleta;
import com.tallerbici.model.Mecanico;
import com.tallerbici.model.OrdenServicio;
import com.tallerbici.model.enums.EstadoOrden;
import com.tallerbici.model.singleton.GestorTaller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador del módulo de Órdenes de Servicio.
 *
 * @author Equipo TallerBici - Programación II
 */
public class OrdenController implements Initializable {

    @FXML private ComboBox<Bicicleta>    cmbBicicleta;
    @FXML private ComboBox<Mecanico>     cmbMecanico;
    @FXML private TextArea               txtMotivo;
    @FXML private TextArea               txtDiagnostico;
    @FXML private TextArea               txtTrabajos;
    @FXML private TextField              txtCosto;
    @FXML private ComboBox<EstadoOrden>  cmbEstado;
    @FXML private Button                 btnAccion;
    @FXML private Label                  lblMensaje;
    @FXML private Label                  lblModoEdicion;
    @FXML private ListView<OrdenServicio> listOrdenes;
    @FXML private TextArea               txtDetalleOrden;

    private GestorTaller gestor = GestorTaller.getInstance();
    private boolean modoEdicion = false;
    private OrdenServicio ordenEnEdicion = null;

    //Inicializa los combos y la lista de ordenes
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbBicicleta.setItems(gestor.getListaBicicletas());
        cmbMecanico.setItems(gestor.getListaMecanicos());
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoOrden.values()));
        cmbEstado.getSelectionModel().selectFirst();
        listOrdenes.setItems(gestor.getListaOrdenes());
    }

    //Decide si se crea una orden nueva o se actualiza una existente
    @FXML
    private void accionPrincipal() {
        if (modoEdicion) {
            actualizarOrden();
        } else {
            crearOrden();
        }
    }

    //Crea una nueva orden de servicio con los datos registrados
    private void crearOrden() {
        try {
            Bicicleta bici = cmbBicicleta.getValue();
            Mecanico mec   = cmbMecanico.getValue();

            if (bici == null) { mostrarError("Selecciona una bicicleta."); return; }
            if (mec == null)  { mostrarError("Selecciona un mecánico."); return; }
            if (txtMotivo.getText().isBlank()) { mostrarError("El motivo es obligatorio."); return; }

            OrdenServicio orden = gestor.crearOrden(
                    bici.getNumeroSerial(),
                    mec.getCodigoInterno(),
                    txtMotivo.getText().trim()
            );
            
            aplicarCamposOpcionales(orden);
            gestor.actualizarOrden(orden);

            mostrarExito("Orden creada: " + orden.getIdOrden());
            limpiarFormulario();

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError("Error" + e.getMessage());
        }
    }

    //Guarda los cambios realizados a un orden
    private void actualizarOrden() {
        try {
            aplicarCamposOpcionales(ordenEnEdicion);
            gestor.actualizarOrden(ordenEnEdicion);
            mostrarExito("Orden actualizada: " + ordenEnEdicion.getIdOrden());
            limpiarFormulario();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError("Error" + e.getMessage());
        }
    }

    //Aplica a la orden el diagnóstico, trabajos, costo y estado en el objeto orden.
    private void aplicarCamposOpcionales(OrdenServicio orden) {
        if (!txtDiagnostico.getText().isBlank()) {
            orden.setDiagnostico(txtDiagnostico.getText().trim());
        }
        if (!txtTrabajos.getText().isBlank()) {
            orden.setTrabajosRealizados(txtTrabajos.getText().trim());
        }
        if (!txtCosto.getText().isBlank()) {
            try {
                double costo = Double.parseDouble(txtCosto.getText().trim());
                orden.setCostoTotal(costo);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("El costo debe ser un número. Ejemplo: 75000");
            }
        }
        EstadoOrden estado = cmbEstado.getValue();
        if (estado != null) {
            orden.setEstado(estado);
        }
    }

    //Carga en el formulario la orden seleccionada
    @FXML
    private void editarSeleccionado() {
        OrdenServicio sel = listOrdenes.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona una orden para editar."); return; }

        ordenEnEdicion = sel;
        Bicicleta bici = gestor.buscarBicicleta(sel.getSerialBicicleta());
        Mecanico mec   = gestor.buscarMecanico(sel.getCodigoMecanico());
        cmbBicicleta.setValue(bici);
        cmbBicicleta.setDisable(true);
        cmbMecanico.setValue(mec);
        cmbMecanico.setDisable(true);
        txtMotivo.setText(sel.getMotivoServicio());
        txtMotivo.setDisable(true);

        txtDiagnostico.setText(sel.getDiagnostico());
        txtTrabajos.setText(sel.getTrabajosRealizados());
        txtCosto.setText(String.valueOf(sel.getCostoTotal()));
        cmbEstado.setValue(sel.getEstado());

        modoEdicion = true;
        btnAccion.setText("💾 Guardar Cambios");
        btnAccion.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white; -fx-font-weight: bold;");
        lblModoEdicion.setText("✏ Editando: " + sel.getIdOrden() + " — solo puedes cambiar diagnóstico, trabajos, costo y estado.");
        lblModoEdicion.setStyle("-fx-text-fill: #E65100; -fx-font-size: 11px; -fx-font-weight: bold;");
    }

    //Muestra el detalle completo de la orden seleccionada
    @FXML
    private void mostrarDetalleOrden(MouseEvent event) {
        OrdenServicio sel = listOrdenes.getSelectionModel().getSelectedItem();
        if (sel != null) {
            String nombreBici = "Desconocida";
            String nombreMec  = "Desconocido";

            Bicicleta bici = gestor.buscarBicicleta(sel.getSerialBicicleta());
            if (bici != null) {
                nombreBici = bici.getMarca() + " " + bici.getTipo();
            }
            Mecanico mec = gestor.buscarMecanico(sel.getCodigoMecanico());
            if (mec != null) {
                nombreMec = mec.getNombreCompleto();
            }

            txtDetalleOrden.setText(sel.getDetalles()
                    + "\n\nBicicleta: " + nombreBici
                    + "\nMecánico:  " + nombreMec);
        }
    }

    //Limpia el formulario y vuelve al modo normal
    @FXML
    public void limpiarFormulario() {
        cmbBicicleta.setDisable(false);
        cmbMecanico.setDisable(false);
        cmbBicicleta.getSelectionModel().clearSelection();
        cmbMecanico.getSelectionModel().clearSelection();
        txtMotivo.clear();
        txtMotivo.setDisable(false);
        txtDiagnostico.clear();
        txtTrabajos.clear();
        txtCosto.clear();
        cmbEstado.getSelectionModel().selectFirst();
        lblMensaje.setText("");
        lblModoEdicion.setText("");
        txtDetalleOrden.clear();
        modoEdicion = false;
        ordenEnEdicion = null;
        btnAccion.setText("Crear Orden");
        btnAccion.setStyle("-fx-background-color: #1A237E; -fx-text-fill: white; -fx-font-weight: bold;");
        listOrdenes.getSelectionModel().clearSelection();
    }

    //Muestra un mensaje de exito en color verde
    private void mostrarExito(String msg) {
        lblMensaje.setStyle("-fx-text-fill: #388E3C; -fx-font-size: 12px;");
        lblMensaje.setText(msg);
    }

    //Muestra un mensaje de error en color rojo
    private void mostrarError(String msg) {
        lblMensaje.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12px;");
        lblMensaje.setText(msg);
    }
}
