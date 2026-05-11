package com.bancaria.model;

import com.bancaria.exception.MontoInvalidoException;
import com.bancaria.exception.SaldoInsuficienteException;
import com.bancaria.repository.TransaccionRepository;
import com.bancaria.service.IOperacionesBancarias;

/**
 * Clase base abstracta que representa una cuenta bancaria genérica.
 *
 * Principio: Encapsulación — saldo y datos internos son privados/protected.
 * Principio: Responsabilidad Única — gestiona el estado de la cuenta y sus operaciones básicas.
 * Principio: Abierto/Cerrado — abierta para extensión (subclases), cerrada para modificación.
 */
public abstract class CuentaBancaria implements IOperacionesBancarias {

    // ── Atributos encapsulados ────────────────────────────────────────────────
    private final String numeroCuenta;
    private final String titular;
    private double saldo;
    protected final TransaccionRepository historial;

    // ── Constructor ──────────────────────────────────────────────────────────
    protected CuentaBancaria(String numeroCuenta, String titular, double saldoInicial) {
        if (numeroCuenta == null || numeroCuenta.isBlank())
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío.");
        if (titular == null || titular.isBlank())
            throw new IllegalArgumentException("El nombre del titular no puede estar vacío.");
        if (saldoInicial < 0)
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo.");

        this.numeroCuenta = numeroCuenta.trim().toUpperCase();
        this.titular = titular.trim();
        this.saldo = saldoInicial;
        this.historial = new TransaccionRepository(this.numeroCuenta);
    }

    // ── Implementación base de IOperacionesBancarias ─────────────────────────

    @Override
    public void depositar(double monto) throws MontoInvalidoException {
        validarMonto(monto, "depositar");
        double saldoAnterior = this.saldo;
        this.saldo += monto;

        historial.registrar(new Transaccion(
                historial.generarId(),
                TipoTransaccion.DEPOSITO,
                monto,
                saldoAnterior,
                this.saldo,
                "Depósito en efectivo",
                this.numeroCuenta
        ));
    }

    @Override
    public void retirar(double monto) throws MontoInvalidoException, SaldoInsuficienteException {
        validarMonto(monto, "retirar");
        verificarFondos(monto);

        double saldoAnterior = this.saldo;
        this.saldo -= monto;

        historial.registrar(new Transaccion(
                historial.generarId(),
                TipoTransaccion.RETIRO,
                monto,
                saldoAnterior,
                this.saldo,
                "Retiro en efectivo",
                this.numeroCuenta
        ));
    }

    // ── Getters (encapsulación) ──────────────────────────────────────────────

    @Override
    public double getSaldo()          { return saldo; }

    @Override
    public String getNumeroCuenta()   { return numeroCuenta; }

    @Override
    public String getTitular()        { return titular; }

    public TransaccionRepository getHistorial() { return historial; }

    // ── Método abstracto (polimorfismo) ──────────────────────────────────────

    /**
     * Cada tipo de cuenta define su propio tipo (ej: "Ahorros", "Corriente").
     */
    public abstract String getTipoCuenta();

    // ── Métodos protegidos para uso en subclases ─────────────────────────────

    /**
     * Permite a las subclases modificar el saldo directamente (por intereses, cargos, etc.)
     * sin exponer el campo al exterior.
     */
    protected void ajustarSaldo(double cantidad) {
        this.saldo += cantidad;
    }

    /**
     * Valida que el monto sea positivo y mayor a cero.
     */
    protected void validarMonto(double monto, String operacion) throws MontoInvalidoException {
        if (monto <= 0) {
            throw new MontoInvalidoException(monto,
                "el monto para '" + operacion + "' debe ser mayor a $0.00");
        }
        if (Double.isNaN(monto) || Double.isInfinite(monto)) {
            throw new MontoInvalidoException(monto, "el monto no es un número válido");
        }
    }

    /**
     * Verifica que haya saldo suficiente para la operación.
     */
    protected void verificarFondos(double monto) throws SaldoInsuficienteException {
        if (this.saldo < monto) {
            throw new SaldoInsuficienteException(this.saldo, monto);
        }
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "┌─ %s ─────────────────────────────┐%n" +
            "│  Cuenta  : %-30s │%n" +
            "│  Titular : %-30s │%n" +
            "│  Saldo   : $%-29.2f │%n" +
            "└──────────────────────────────────────────┘",
            getTipoCuenta(),
            numeroCuenta,
            titular,
            saldo
        );
    }
}
