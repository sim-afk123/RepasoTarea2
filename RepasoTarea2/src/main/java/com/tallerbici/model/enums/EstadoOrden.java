package com.tallerbici.model.enums;

/**
 * estados en que pueden estar las ordenes
 *
 * @author Equipo TallerBici - Programación II
 */
public enum EstadoOrden {
    INGRESADA("Ingresada"),
    EN_PROCESO("En Proceso"),
    LISTA("Lista"),
    ENTREGADA("Entregada"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoOrden(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}