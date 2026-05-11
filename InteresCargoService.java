package com.bancaria.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase inmutable que representa una transacción bancaria.
 * Principio: Responsabilidad Única — solo almacena datos de una transacción.
 * Principio: Encapsulación — todos los campos son privados y solo lectura.
 */
public class Transaccion {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String id;
    private final TipoTransaccion tipo;
    private final double monto;
    private final double saldoAntes;
    private final double saldoDespues;
    private final String descripcion;
    private final LocalDateTime fechaHora;
    private final String cuentaOrigen;
    private final String cuentaDestino;

    // Constructor para transacciones simples (depósito, retiro, cargos)
    public Transaccion(String id, TipoTransaccion tipo, double monto,
                       double saldoAntes, double saldoDespues,
                       String descripcion, String cuentaOrigen) {
        this(id, tipo, monto, saldoAntes, saldoDespues, descripcion, cuentaOrigen, null);
    }

    // Constructor completo para transferencias
    public Transaccion(String id, TipoTransaccion tipo, double monto,
                       double saldoAntes, double saldoDespues,
                       String descripcion, String cuentaOrigen, String cuentaDestino) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoAntes = saldoAntes;
        this.saldoDespues = saldoDespues;
        this.descripcion = descripcion;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.fechaHora = LocalDateTime.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId()             { return id; }
    public TipoTransaccion getTipo()  { return tipo; }
    public double getMonto()          { return monto; }
    public double getSaldoAntes()     { return saldoAntes; }
    public double getSaldoDespues()   { return saldoDespues; }
    public String getDescripcion()    { return descripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getCuentaOrigen()   { return cuentaOrigen; }
    public String getCuentaDestino()  { return cuentaDestino; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  [%s] %s", fechaHora.format(FORMATTER), tipo.getDescripcion()));
        sb.append(String.format("\n  ID: %s", id));
        sb.append(String.format("\n  Monto     : $%,.2f", monto));
        sb.append(String.format("\n  Saldo ant.: $%,.2f  →  Saldo nuevo: $%,.2f", saldoAntes, saldoDespues));
        if (cuentaDestino != null) {
            sb.append(String.format("\n  Cuenta destino: %s", cuentaDestino));
        }
        if (descripcion != null && !descripcion.isBlank()) {
            sb.append(String.format("\n  Nota: %s", descripcion));
        }
        return sb.toString();
    }
}
