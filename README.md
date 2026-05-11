# 🏦 Cuenta Bancaria — Java POO

Proyecto académico de la **Universidad de San Buenaventura, Bogotá**  
Asignatura: Ingeniería de Software / Arquitectura de Software

---

## 📌 Descripción

Ampliación del proyecto base **Cuenta Bancaria** aplicando los cuatro principios POO trabajados en clase:

| Principio | Aplicación en el proyecto |
|---|---|
| **Encapsulación** | `saldo`, `titular` y `numeroCuenta` son privados; solo accesibles vía getters |
| **Responsabilidad Única** | Cada clase/servicio tiene una sola razón de cambio |
| **Abierto/Cerrado** | `CuentaBancaria` se extiende sin modificarse (`CuentaAhorros`, `CuentaCorriente`) |
| **Inversión de Dependencias** | Los servicios operan sobre la interfaz `IOperacionesBancarias`, no sobre clases concretas |

---

## ✨ Nuevas Funcionalidades

### 1. Transferencias entre cuentas (`TransferenciaService`)
- Transfiere fondos entre cualquier par de cuentas registradas en el banco.
- Validaciones: monto mínimo ($1.000), máximo ($50.000.000), misma cuenta, cuentas nulas.
- Operación **atómica**: si el depósito en destino falla, el retiro del origen se revierte.
- Registra `TRANSFERENCIA_ENVIADA` en la cuenta origen y `TRANSFERENCIA_RECIBIDA` en la destino.

### 2. Historial de transacciones (`TransaccionRepository`)
- Cada cuenta mantiene su propio repositorio de transacciones inmutables.
- Permite consultar: historial completo, últimas N, filtrar por tipo.
- Cada transacción almacena: ID correlativo, tipo, monto, saldo antes/después, fecha-hora y notas.

### 3. Aplicación de intereses y cargos (`InteresCargoService`)
- **Interés mensual** (Cuenta Ahorros): aplica interés compuesto sobre el saldo.
- **Cargo de mantenimiento** (Cuenta Corriente): descuenta cargo fijo mensual ($15.000 COP).
- **Cargo por sobregiro** (Cuenta Corriente): cargo adicional si hay saldo negativo ($25.000 COP).
- **Proyección de crecimiento**: tabla de crecimiento a N meses sin modificar la cuenta.

---

## 🗂 Estructura del proyecto

```
CuentaBancaria/
└── src/main/java/com/bancaria/
    ├── Main.java
    ├── model/
    │   ├── TipoTransaccion.java     (enum)
    │   ├── Transaccion.java         (registro inmutable)
    │   ├── CuentaBancaria.java      (clase abstracta base)
    │   ├── CuentaAhorros.java       (extiende CuentaBancaria)
    │   └── CuentaCorriente.java     (extiende CuentaBancaria)
    ├── exception/
    │   ├── SaldoInsuficienteException.java
    │   ├── CuentaInvalidaException.java
    │   └── MontoInvalidoException.java
    ├── repository/
    │   └── TransaccionRepository.java
    ├── service/
    │   ├── IOperacionesBancarias.java   (interfaz — inversión de dependencias)
    │   ├── BancoService.java            (registro central de cuentas)
    │   ├── TransferenciaService.java    ← Funcionalidad 1
    │   └── InteresCargoService.java     ← Funcionalidad 2 y 3
    └── ui/
        └── ConsoleUI.java
```

---

## ▶ Compilación y ejecución

### Requisitos
- Java 17 o superior (se usan *pattern matching* para `instanceof`)

### Desde la raíz del proyecto

```bash
# 1. Compilar
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# 2. Ejecutar
java -cp out com.bancaria.Main
```

### Desde un IDE (IntelliJ / Eclipse / VS Code)
1. Importar la carpeta `CuentaBancaria` como proyecto Java.
2. Marcar `src/main/java` como **Sources Root**.
3. Ejecutar `Main.java`.

---

## 💡 Cuentas de demostración

El sistema crea automáticamente 4 cuentas al iniciar:

| N° Cuenta | Titular | Tipo | Saldo inicial |
|---|---|---|---|
| AH-001 | Diego García | Ahorros (4% mensual) | $2.000.000 |
| AH-002 | Laura Martínez | Ahorros (3.5% mensual) | $500.000 |
| CC-001 | Diego García | Corriente (cupo $500k) | $800.000 |
| CC-002 | TechCorp S.A.S | Corriente (cupo $2M) | $5.000.000 |

---

## 🔒 Manejo de errores

| Excepción | Cuándo se lanza |
|---|---|
| `MontoInvalidoException` | Monto ≤ 0 o fuera de límites |
| `SaldoInsuficienteException` | Fondos insuficientes para la operación |
| `CuentaInvalidaException` | Cuenta no encontrada, nula o duplicada |

---

*Proyecto generado para la Facultad de Ingeniería — USB Bogotá*
