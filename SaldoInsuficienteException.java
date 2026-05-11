package com.bancaria.model;

/**
 * Enumeración que representa los tipos de transacción disponibles en el sistema.
 * Principio: Responsabilidad Única — solo define categorías de transacción.
 */
public enum TipoTransaccion {
    DEPOSITO("Depósito"),
    RETIRO("Retiro"),
    TRANSFERENCIA_ENVIADA("Transferencia Enviada"),
    TRANSFERENCIA_RECIBIDA("Transferencia Recibida"),
    APLICACION_INTERES("Aplicación de Interés"),
    CARGO_MANTENIMIENTO("Cargo por Mantenimiento"),
    CARGO_SOBREGIRO("Cargo por Sobregiro");

    private final String descripcion;

    TipoTransaccion(String descripcion) {
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
