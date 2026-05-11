package com.bancaria.ui;

import com.bancaria.exception.CuentaInvalidaException;
import com.bancaria.exception.MontoInvalidoException;
import com.bancaria.exception.SaldoInsuficienteException;
import com.bancaria.model.CuentaAhorros;
import com.bancaria.model.CuentaBancaria;
import com.bancaria.model.CuentaCorriente;
import com.bancaria.service.BancoService;
import com.bancaria.service.InteresCargoService;
import com.bancaria.service.TransferenciaService;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Interfaz de usuario en consola para interactuar con el sistema bancario.
 * Principio: Responsabilidad Única — solo gestiona la presentación e interacción.
 */
public class ConsoleUI {

    private final BancoService        bancoService;
    private final TransferenciaService transferenciaService;
    private final InteresCargoService  interesCargoService;
    private final Scanner             scanner;

    public ConsoleUI(BancoService bancoService) {
        this.bancoService         = bancoService;
        this.transferenciaService = new TransferenciaService();
        this.interesCargoService  = new InteresCargoService();
        this.scanner              = new Scanner(System.in);
    }

    // ── Menú principal ────────────────────────────────────────────────────────

    public void iniciar() {
        System.out.println("\n  ╔══════════════════════════════════════════╗");
        System.out.println("  ║       SISTEMA BANCARIO — JAVA POO        ║");
        System.out.println("  ╚══════════════════════════════════════════╝");

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("  Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1  -> menuOperacionesCuenta();
                case 2  -> menuTransferencias();
                case 3  -> menuInteresesCargos();
                case 4  -> menuHistorial();
                case 5  -> bancoService.imprimirResumen();
                case 0  -> continuar = false;
                default -> System.out.println("  ✖ Opción inválida. Intente de nuevo.");
            }
        }

        System.out.println("\n  ¡Hasta luego!\n");
        scanner.close();
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n  ─── MENÚ PRINCIPAL ─────────────────────────");
        System.out.println("  1. Operaciones de cuenta (depósito / retiro)");
        System.out.println("  2. Transferencias entre cuentas");
        System.out.println("  3. Intereses y cargos");
        System.out.println("  4. Historial de transacciones");
        System.out.println("  5. Ver resumen de cuentas");
        System.out.println("  0. Salir");
        System.out.println("  ────────────────────────────────────────────");
    }

    // ── Sub-menú: operaciones básicas ────────────────────────────────────────

    private void menuOperacionesCuenta() {
        CuentaBancaria cuenta = seleccionarCuenta();
        if (cuenta == null) return;

        System.out.println("\n  1. Depositar");
        System.out.println("  2. Retirar");
        System.out.println("  3. Ver saldo");
        int op = leerEntero("  Opción: ");

        switch (op) {
            case 1 -> {
                double monto = leerDouble("  Monto a depositar: $");
                try {
                    cuenta.depositar(monto);
                    System.out.printf("  ✔ Depósito exitoso. Saldo actual: $%,.2f%n", cuenta.getSaldo());
                } catch (MontoInvalidoException e) {
                    System.out.println("  ✖ Error: " + e.getMessage());
                }
            }
            case 2 -> {
                double monto = leerDouble("  Monto a retirar: $");
                try {
                    cuenta.retirar(monto);
                    System.out.printf("  ✔ Retiro exitoso. Saldo actual: $%,.2f%n", cuenta.getSaldo());
                } catch (MontoInvalidoException | SaldoInsuficienteException e) {
                    System.out.println("  ✖ Error: " + e.getMessage());
                }
            }
            case 3 -> System.out.println(cuenta);
            default -> System.out.println("  ✖ Opción inválida.");
        }
    }

    // ── Sub-menú: transferencias ─────────────────────────────────────────────

    private void menuTransferencias() {
        System.out.println("\n  ─── TRANSFERENCIAS ──");
        System.out.print("  Número de cuenta ORIGEN : ");
        String numOrigen = scanner.nextLine().trim();

        System.out.print("  Número de cuenta DESTINO: ");
        String numDestino = scanner.nextLine().trim();

        double monto = leerDouble("  Monto a transferir    : $");

        System.out.print("  Nota/descripción (Enter para omitir): ");
        String nota = scanner.nextLine().trim();

        try {
            CuentaBancaria origen  = bancoService.buscarCuenta(numOrigen);
            CuentaBancaria destino = bancoService.buscarCuenta(numDestino);
            transferenciaService.transferir(origen, destino, monto, nota);
        } catch (CuentaInvalidaException | MontoInvalidoException | SaldoInsuficienteException e) {
            System.out.println("  ✖ Error en transferencia: " + e.getMessage());
        }
    }

    // ── Sub-menú: intereses y cargos ─────────────────────────────────────────

    private void menuInteresesCargos() {
        CuentaBancaria cuenta = seleccionarCuenta();
        if (cuenta == null) return;

        System.out.println("\n  1. Aplicar interés mensual    (Ahorros)");
        System.out.println("  2. Aplicar cargo mantenimiento (Corriente)");
        System.out.println("  3. Aplicar cargo por sobregiro (Corriente)");
        System.out.println("  4. Proyectar crecimiento a N meses (Ahorros)");
        int op = leerEntero("  Opción: ");

        switch (op) {
            case 1 -> {
                if (cuenta instanceof CuentaAhorros ca) {
                    interesCargoService.aplicarInteresMensual(ca);
                } else {
                    System.out.println("  ✖ Esta operación es solo para Cuentas de Ahorros.");
                }
            }
            case 2 -> {
                if (cuenta instanceof CuentaCorriente cc) {
                    interesCargoService.aplicarCargoMantenimiento(cc);
                } else {
                    System.out.println("  ✖ Esta operación es solo para Cuentas Corrientes.");
                }
            }
            case 3 -> {
                if (cuenta instanceof CuentaCorriente cc) {
                    interesCargoService.aplicarCargoSobregiro(cc);
                } else {
                    System.out.println("  ✖ Esta operación es solo para Cuentas Corrientes.");
                }
            }
            case 4 -> {
                if (cuenta instanceof CuentaAhorros ca) {
                    int meses = leerEntero("  ¿Cuántos meses proyectar? (1-360): ");
                    interesCargoService.proyectarCrecimiento(ca, meses);
                } else {
                    System.out.println("  ✖ Esta operación es solo para Cuentas de Ahorros.");
                }
            }
            default -> System.out.println("  ✖ Opción inválida.");
        }
    }

    // ── Sub-menú: historial ──────────────────────────────────────────────────

    private void menuHistorial() {
        CuentaBancaria cuenta = seleccionarCuenta();
        if (cuenta == null) return;

        System.out.println("\n  1. Ver historial completo");
        System.out.println("  2. Ver últimas 5 transacciones");
        System.out.println("  3. Ver últimas N transacciones");
        int op = leerEntero("  Opción: ");

        System.out.printf("%n  ─── Historial: [%s] %s ──────────────────────%n",
                cuenta.getNumeroCuenta(), cuenta.getTitular());

        switch (op) {
            case 1 -> cuenta.getHistorial().imprimirHistorial();
            case 2 -> cuenta.getHistorial().imprimirUltimas(5);
            case 3 -> {
                int n = leerEntero("  ¿Cuántas transacciones mostrar? ");
                cuenta.getHistorial().imprimirUltimas(n);
            }
            default -> System.out.println("  ✖ Opción inválida.");
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private CuentaBancaria seleccionarCuenta() {
        bancoService.imprimirResumen();
        System.out.print("\n  Ingrese el número de cuenta: ");
        String num = scanner.nextLine().trim();
        try {
            return bancoService.buscarCuenta(num);
        } catch (CuentaInvalidaException e) {
            System.out.println("  ✖ " + e.getMessage());
            return null;
        }
    }

    private int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int valor = scanner.nextInt();
                scanner.nextLine(); // consumir salto de línea
                return valor;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("  ✖ Ingrese un número entero válido.");
            }
        }
    }

    private double leerDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double valor = scanner.nextDouble();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("  ✖ Ingrese un valor numérico válido (use '.' como decimal).");
            }
        }
    }
}
