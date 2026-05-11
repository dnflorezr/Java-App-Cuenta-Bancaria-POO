package com.bancaria.repository;

import com.bancaria.model.Transaccion;
import com.bancaria.model.TipoTransaccion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositorio que gestiona el historial de transacciones de una cuenta.
 * Principio: Responsabilidad Única — solo se encarga de almacenar y consultar transacciones.
 * Principio: Encapsulación — la lista interna no se expone directamente.
 *
 * FUNCIONALIDAD 1: Historial de Transacciones
 */
public class TransaccionRepository {

    private final List<Transaccion> transacciones;
    private final String numeroCuenta;
    private int contadorId;

    public TransaccionRepository(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
        this.transacciones = new ArrayList<>();
        this.contadorId = 1;
    }

    /**
     * Genera un ID único y correlativo para cada transacción.
     */
    public String generarId() {
        return String.format("TXN-%s-%04d", numeroCuenta, contadorId++);
    }

    /**
     * Registra una nueva transacción en el historial.
     */
    public void registrar(Transaccion transaccion) {
        transacciones.add(transaccion);
    }

    /**
     * Retorna todas las transacciones (vista inmutable).
     */
    public List<Transaccion> obtenerTodas() {
        return Collections.unmodifiableList(transacciones);
    }

    /**
     * Retorna las últimas N transacciones.
     */
    public List<Transaccion> obtenerUltimas(int n) {
        int size = transacciones.size();
        int desde = Math.max(0, size - n);
        return Collections.unmodifiableList(transacciones.subList(desde, size));
    }

    /**
     * Filtra transacciones por tipo.
     */
    public List<Transaccion> filtrarPorTipo(TipoTransaccion tipo) {
        return transacciones.stream()
                .filter(t -> t.getTipo() == tipo)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retorna el total de registros en el historial.
     */
    public int totalTransacciones() {
        return transacciones.size();
    }

    /**
     * Imprime el historial completo con separadores visuales.
     */
    public void imprimirHistorial() {
        if (transacciones.isEmpty()) {
            System.out.println("  No hay transacciones registradas.");
            return;
        }
        System.out.printf("  Total de movimientos: %d%n%n", transacciones.size());
        for (Transaccion t : transacciones) {
            System.out.println("  " + "─".repeat(50));
            System.out.println(t);
        }
        System.out.println("  " + "─".repeat(50));
    }

    /**
     * Imprime solo las últimas N transacciones.
     */
    public void imprimirUltimas(int n) {
        List<Transaccion> ultimas = obtenerUltimas(n);
        System.out.printf("  Últimas %d transacciones:%n%n", ultimas.size());
        for (Transaccion t : ultimas) {
            System.out.println("  " + "─".repeat(50));
            System.out.println(t);
        }
        System.out.println("  " + "─".repeat(50));
    }
}
