package com.dajham.bankcore.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio Transaction (Transacción bancaria).
 * Representa una transferencia de fondos entre dos cuentas.
 * Esta clase es Java puro sin dependencias de frameworks.
 */
public class Transaction {

    private Long id;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String referenceCode;

    /**
     * Constructor vacío.
     */
    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.referenceCode = UUID.randomUUID().toString();
    }

    /**
     * Constructor completo.
     */
    public Transaction(Long id, Long sourceAccountId, Long targetAccountId,
            BigDecimal amount, LocalDateTime timestamp, String referenceCode) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.referenceCode = referenceCode != null ? referenceCode : UUID.randomUUID().toString();
    }

    /**
     * Constructor para crear nueva transacción.
     */
    public Transaction(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        this(null, sourceAccountId, targetAccountId, amount, LocalDateTime.now(), UUID.randomUUID().toString());
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(referenceCode, that.referenceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, referenceCode);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sourceAccountId=" + sourceAccountId +
                ", targetAccountId=" + targetAccountId +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", referenceCode='" + referenceCode + '\'' +
                '}';
    }
}
