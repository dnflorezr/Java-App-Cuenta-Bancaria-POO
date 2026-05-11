package com.bancaria.model;

import com.bancaria.exception.MontoInvalidoException;
import com.bancaria.exception.SaldoInsuficienteException;

/**
 * Cuenta de Ahorros: genera intereses periódicos y no tiene sobregiro.
 *
 * Principio: Abierto/Cerrado — extiende CuentaBancaria sin modificar su lógica base.
 * Principio: Encapsulación — la tasa de interés está encapsulada con validación.
 */
public class CuentaAhorros extends CuentaBancaria {

    private static final double TASA_INTERES_MINIMA  = 0.001;  // 0.1%
    private static final double TASA_INTERES_MAXIMA  = 0.30;   // 30%
    private static final double SALDO_MINIMO         = 50_000; // COP

    private double tasaInteres;       // Tasa mensual (ej: 0.04 = 4%)
    private int    periodosMes;       // Contador de periodos aplicados

    public CuentaAhorros(String numeroCuenta, String titular,
                         double saldoInicial, double tasaInteresMensual) {
        super(numeroCuenta, titular, saldoInicial);
        setTasaInteres(tasaInteresMensual);
        this.periodosMes = 0;
    }

    // ── Sobrescritura de retirar: saldo mínimo requerido ─────────────────────

    @Override
    public void retirar(double monto) throws MontoInvalidoException, SaldoInsuficienteException {
        validarMonto(monto, "retirar");
        double saldoRestante = getSaldo() - monto;
        if (saldoRestante < SALDO_MINIMO) {
            throw new SaldoInsuficienteException(getSaldo(),
                monto + SALDO_MINIMO /* monto + mínimo requerido */);
        }
        super.retirar(monto);
    }

    // ── Getters / Setters con validación ─────────────────────────────────────

    public double getTasaInteres()    { return tasaInteres; }
    public int    getPeriodosMes()    { return periodosMes; }
    public double getSaldoMinimo()    { return SALDO_MINIMO; }

    public void setTasaInteres(double tasa) {
        if (tasa < TASA_INTERES_MINIMA || tasa > TASA_INTERES_MAXIMA) {
            throw new IllegalArgumentException(String.format(
                "Tasa inválida: %.4f. Rango permitido: [%.1f%% - %.0f%%]",
                tasa, TASA_INTERES_MINIMA * 100, TASA_INTERES_MAXIMA * 100
            ));
        }
        this.tasaInteres = tasa;
    }

    /**
     * Incrementa el contador de periodos (para uso del servicio de intereses).
     */
    public void incrementarPeriodo() { this.periodosMes++; }

    /**
     * Acredita el interés calculado externamente al saldo de la cuenta.
     * Exponemos este método público para que el servicio pueda operar
     * sin romper la encapsulación del saldo base.
     */
    public void acreditarInteres(double interes) {
        ajustarSaldo(interes);
    }

    @Override
    public String getTipoCuenta() { return "Cuenta de Ahorros"; }

    @Override
    public String toString() {
        return super.toString() + String.format(
            "%n│  Tasa    : %.2f%% mensual                    │" +
            "%n│  Periodos: %-30d │",
            tasaInteres * 100, periodosMes
        );
    }
}
