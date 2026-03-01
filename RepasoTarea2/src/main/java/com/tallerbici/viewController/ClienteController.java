package com.tallerbici.viewController;

import com.tallerbici.model.Cliente;
import com.tallerbici.model.singleton.GestorTaller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controlador del módulo de Clientes.
 * Soporta registrar y editar clientes.
 * Tiene un modo normal y un modo edicion
 * @author Equipo TallerBici - Programación II
 */
public class ClienteController implements Initializable {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private Button    btnAccion;
    @FXML private Label     lblMensaje;
    @FXML private Label     lblModoEdicion;
    @FXML private ListView<Cliente> listClientes;
    @FXML private TextField txtBuscarCliente;
    @FXML private TextArea  txtDetalleCliente;

    private GestorTaller gestor = GestorTaller.getInstance();
    private boolean modoEdicion = false;

    //Conecta la lista de clientes con el gestor
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listClientes.setItems(gestor.getListaClientes());
    }

    //Decide si registra un cliente nuevo o actualiza uno existente
    @FXML
    private void accionPrincipal() {
        if (modoEdicion) {
            actualizarCliente();
        } else {
            registrarCliente();
        }
    }

    //Registra un nuevo cliente con los datos del formulario
    private void registrarCliente() {
        try {
            Cliente nuevo = new Cliente.Builder()
                    .numeroCedula(txtCedula.getText().trim())
                    .nombreCompleto(txtNombre.getText().trim())
                    .telefono(txtTelefono.getText().trim())
                    .direccion(txtDireccion.getText().trim())
                    .build();

            gestor.registrarCliente(nuevo);
            mostrarExito("Cliente registrado: " + nuevo.getNombreCompleto());
            limpiarFormulario();

        } catch (IllegalStateException | IllegalArgumentException e) {
            mostrarError("Error" + e.getMessage());
        }
    }

    //Actualiza la informacion de un cliente que ya existe
    private void actualizarCliente() {
        try {
            Cliente actualizado = new Cliente.Builder()
                    .numeroCedula(txtCedula.getText().trim())
                    .nombreCompleto(txtNombre.getText().trim())
                    .telefono(txtTelefono.getText().trim())
                    .direccion(txtDireccion.getText().trim())
                    .build();

            gestor.actualizarCliente(actualizado);
            mostrarExito("Cliente actualizado: " + actualizado.getNombreCompleto());
            limpiarFormulario();

        } catch (IllegalStateException | IllegalArgumentException e) {
            mostrarError("Error" + e.getMessage());
        }
    }

    //Carga el formulario del cliente seleccionado
    @FXML
    private void editarSeleccionado() {
        Cliente seleccionado = listClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un cliente de la lista para editar.");
            return;
        }
        txtCedula.setText(seleccionado.getNumeroCedula());
        txtCedula.setDisable(true);
        txtNombre.setText(seleccionado.getNombreCompleto());
        txtTelefono.setText(seleccionado.getTelefono());
        txtDireccion.setText(seleccionado.getDireccion());
        
        modoEdicion = true;
        btnAccion.setText("Guardar Cambios");
        btnAccion.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white; -fx-font-weight: bold;");
        lblModoEdicion.setText("✏ Editando: " + seleccionado.getNombreCompleto());
        lblModoEdicion.setStyle("-fx-text-fill: #E65100; -fx-font-weight: bold;");
    }

//Filtra la lista de clientes segun el texto escrito
    @FXML
    private void filtrarClientes() {
        String criterio = txtBuscarCliente.getText().trim().toLowerCase();

        if (criterio.isEmpty()) {
            listClientes.setItems(gestor.getListaClientes());
        } else {
            ArrayList<Cliente> filtrados = new ArrayList<>();
            for (Cliente c : gestor.getListaClientes()) {
                if (c.coincideCon(criterio)) {
                    filtrados.add(c);
                }
            }
            listClientes.setItems(FXCollections.observableArrayList(filtrados));
        }
    }

//Muestra los detalles del cliente seleccionado
    @FXML
    private void mostrarDetalleCliente(MouseEvent event) {
        Cliente seleccionado = listClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtDetalleCliente.setText(seleccionado.getDetalles());
        }
    }

    //Limpia todos los campos del formulario
    @FXML
    public void limpiarFormulario() {
        txtCedula.clear();
        txtCedula.setDisable(false);
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        lblMensaje.setText("");
        lblModoEdicion.setText("");
        txtDetalleCliente.clear();
        txtBuscarCliente.clear();
        modoEdicion = false;
        btnAccion.setText("Registrar Cliente");
        btnAccion.setStyle("-fx-background-color: #1A237E; -fx-text-fill: white; -fx-font-weight: bold;");
        listClientes.setItems(gestor.getListaClientes());
        listClientes.getSelectionModel().clearSelection();
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
