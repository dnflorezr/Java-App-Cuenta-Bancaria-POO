package com.bancaria.model;

import com.bancaria.exception.MontoInvalidoException;
import com.bancaria.exception.SaldoInsuficienteException;

/**
 * Cuenta Corriente: permite sobregiro hasta un cupo definido
 * y cobra cargo fijo mensual de mantenimiento.
 *
 * Principio: Abierto/Cerrado — extiende sin modificar la clase base.
 */
public class CuentaCorriente extends CuentaBancaria {

    private static final double CARGO_MANTENIMIENTO_DEFAULT = 15_000.0; // COP

    private final double cupoSobregiro;         // Límite de sobregiro permitido
    private final double cargoMantenimiento;    // Cargo mensual fijo
    private boolean sobregiroActivo;

    public CuentaCorriente(String numeroCuenta, String titular,
                           double saldoInicial, double cupoSobregiro) {
        super(numeroCuenta, titular, saldoInicial);
        if (cupoSobregiro < 0)
            throw new IllegalArgumentException("El cupo de sobregiro no puede ser negativo.");
        this.cupoSobregiro = cupoSobregiro;
        this.cargoMantenimiento = CARGO_MANTENIMIENTO_DEFAULT;
        this.sobregiroActivo = false;
    }

    // ── Sobrescritura de retirar: permite sobregiro ───────────────────────────

    @Override
    public void retirar(double monto) throws MontoInvalidoException, SaldoInsuficienteException {
        validarMonto(monto, "retirar");

        double disponible = getSaldo() + cupoSobregiro;
        if (monto > disponible) {
            throw new SaldoInsuficienteException(disponible, monto);
        }

        double saldoAnterior = getSaldo();
        ajustarSaldo(-monto);

        if (getSaldo() < 0) {
            sobregiroActivo = true;
        }

        historial.registrar(new Transaccion(
                historial.generarId(),
                TipoTransaccion.RETIRO,
                monto,
                saldoAnterior,
                getSaldo(),
                sobregiroActivo ? "Retiro con sobregiro activo" : "Retiro en efectivo",
                getNumeroCuenta()
        ));
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public double  getCupoSobregiro()       { return cupoSobregiro; }
    public double  getCargoMantenimiento()  { return cargoMantenimiento; }
    public boolean isSobregiroActivo()      { return sobregiroActivo; }

    public void    setSobregiroActivo(boolean estado) { this.sobregiroActivo = estado; }

    /** Aplica un cargo (monto negativo) al saldo de la cuenta. */
    public void aplicarCargo(double cargo) {
        ajustarSaldo(-cargo);
    }

    @Override
    public String getTipoCuenta() { return "Cuenta Corriente"; }

    @Override
    public String toString() {
        return super.toString() + String.format(
            "%n│  Sobregiro: $%-28,.2f │" +
            "%n│  Mant.    : $%-28,.2f │",
            cupoSobregiro, cargoMantenimiento
        );
    }
}
