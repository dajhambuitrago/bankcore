package com.dajham.bankcore.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad de dominio Account (Cuenta bancaria).
 * Esta clase representa el núcleo del negocio y NO debe tener dependencias de
 * frameworks.
 * Es Java puro siguiendo los principios de Clean Architecture.
 */
public class Account {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Long userId;
    /**
     * Campo de control de concurrencia optimista.
     * Se mapea desde la capa de persistencia para evitar sobrescrituras.
     */
    private Long version;

    /**
     * Constructor vacío para frameworks de persistencia y deserialización.
     */
    public Account() {
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Constructor con parámetros para crear una cuenta.
     */
    public Account(Long id, String accountNumber, BigDecimal balance, Long userId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.userId = userId;
    }

    /**
     * Constructor sin ID para nuevas cuentas.
     */
    public Account(String accountNumber, BigDecimal balance, Long userId) {
        this(null, accountNumber, balance, userId);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getVersion() {
        return version;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Métodos de negocio (Domain Logic)

    /**
     * Deposita una cantidad en la cuenta.
     * 
     * @param amount Cantidad a depositar
     * @throws IllegalArgumentException si el monto es negativo o cero
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor a cero");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Retira una cantidad de la cuenta.
     * 
     * @param amount Cantidad a retirar
     * @throws IllegalArgumentException si el monto es negativo, cero o insuficiente
     */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a cero");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el retiro");
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Verifica si la cuenta tiene saldo suficiente.
     * 
     * @param amount Cantidad a verificar
     * @return true si hay saldo suficiente, false en caso contrario
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", userId=" + userId +
                '}';
    }
}
