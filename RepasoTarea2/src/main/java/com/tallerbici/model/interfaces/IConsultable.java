package com.tallerbici.model.interfaces;

/**
 * Interfaz para entidades que pueden consultarse por múltiples criterios.
 * Principio I (ISP): separamos la consulta del registro.
 *
 * @author Equipo TallerBici - Programación II
 */
public interface IConsultable {

    /**
     * Retorna los datos completos de la entidad en formato de texto.
     * @return String con toda la información
     */
    String getDetalles();

    /**
     * Verifica si la entidad coincide con un criterio de búsqueda.
     * @param criterio texto a buscar
     * @return true si coincide, false si no
     */
    boolean coincideCon(String criterio);
}