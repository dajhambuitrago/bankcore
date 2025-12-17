package com.dajham.bankcore.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Account.
 * Verifica la lógica de negocio de depósitos y retiros.
 */
@DisplayName("Account - Domain Model Tests")
class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        // Arrange: Crear cuenta con balance inicial de 1000
        account = new Account(
                1L,
                "ACC-1234567890",
                new BigDecimal("1000.00"),
                100L);
    }

    @Test
    @DisplayName("deposit() debe incrementar el balance cuando el monto es positivo")
    void deposit_ShouldIncreaseBalance_WhenAmountIsPositive() {
        // Arrange
        BigDecimal initialBalance = account.getBalance();
        BigDecimal depositAmount = new BigDecimal("500.00");
        BigDecimal expectedBalance = initialBalance.add(depositAmount);

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(expectedBalance, account.getBalance(),
                "El balance debe incrementarse en el monto depositado");
        assertEquals(new BigDecimal("1500.00"), account.getBalance(),
                "El balance final debe ser 1500.00");
    }

    @Test
    @DisplayName("withdraw() debe decrementar el balance cuando hay fondos suficientes")
    void withdraw_ShouldDecreaseBalance_WhenFundsAreSufficient() {
        // Arrange
        BigDecimal initialBalance = account.getBalance();
        BigDecimal withdrawAmount = new BigDecimal("300.00");
        BigDecimal expectedBalance = initialBalance.subtract(withdrawAmount);

        // Act
        account.withdraw(withdrawAmount);

        // Assert
        assertEquals(expectedBalance, account.getBalance(),
                "El balance debe decrementarse en el monto retirado");
        assertEquals(new BigDecimal("700.00"), account.getBalance(),
                "El balance final debe ser 700.00");
    }

    @Test
    @DisplayName("withdraw() debe lanzar excepción cuando el balance es insuficiente")
    void withdraw_ShouldThrowException_WhenBalanceIsInsufficient() {
        // Arrange
        BigDecimal withdrawAmount = new BigDecimal("2000.00"); // Mayor al balance de 1000

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(withdrawAmount),
                "Debe lanzar IllegalArgumentException cuando el saldo es insuficiente");

        assertEquals("Saldo insuficiente para realizar el retiro", exception.getMessage(),
                "El mensaje de error debe indicar saldo insuficiente");

        // Verificar que el balance no cambió
        assertEquals(new BigDecimal("1000.00"), account.getBalance(),
                "El balance no debe cambiar cuando falla el retiro");
    }

    @Test
    @DisplayName("withdraw() debe lanzar excepción cuando el monto es negativo")
    void withdraw_ShouldThrowException_WhenAmountIsNegative() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-50.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(negativeAmount),
                "Debe lanzar IllegalArgumentException cuando el monto es negativo");

        assertEquals("El monto a retirar debe ser mayor a cero", exception.getMessage(),
                "El mensaje debe indicar que el monto debe ser positivo");

        // Verificar que el balance no cambió
        assertEquals(new BigDecimal("1000.00"), account.getBalance(),
                "El balance no debe cambiar cuando el monto es inválido");
    }

    @Test
    @DisplayName("withdraw() debe lanzar excepción cuando el monto es cero")
    void withdraw_ShouldThrowException_WhenAmountIsZero() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(zeroAmount),
                "Debe lanzar IllegalArgumentException cuando el monto es cero");

        assertEquals("El monto a retirar debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("deposit() debe lanzar excepción cuando el monto es negativo")
    void deposit_ShouldThrowException_WhenAmountIsNegative() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-100.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(negativeAmount),
                "Debe lanzar IllegalArgumentException cuando el monto de depósito es negativo");

        assertEquals("El monto a depositar debe ser mayor a cero", exception.getMessage());

        // Verificar que el balance no cambió
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    @Test
    @DisplayName("deposit() debe lanzar excepción cuando el monto es cero")
    void deposit_ShouldThrowException_WhenAmountIsZero() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(zeroAmount),
                "Debe lanzar IllegalArgumentException cuando el monto de depósito es cero");
    }

    @Test
    @DisplayName("hasSufficientBalance() debe retornar true cuando hay fondos suficientes")
    void hasSufficientBalance_ShouldReturnTrue_WhenBalanceIsSufficient() {
        // Arrange
        BigDecimal amount = new BigDecimal("500.00");

        // Act
        boolean result = account.hasSufficientBalance(amount);

        // Assert
        assertTrue(result, "Debe retornar true cuando hay fondos suficientes");
    }

    @Test
    @DisplayName("hasSufficientBalance() debe retornar false cuando hay fondos insuficientes")
    void hasSufficientBalance_ShouldReturnFalse_WhenBalanceIsInsufficient() {
        // Arrange
        BigDecimal amount = new BigDecimal("2000.00");

        // Act
        boolean result = account.hasSufficientBalance(amount);

        // Assert
        assertFalse(result, "Debe retornar false cuando hay fondos insuficientes");
    }
}
