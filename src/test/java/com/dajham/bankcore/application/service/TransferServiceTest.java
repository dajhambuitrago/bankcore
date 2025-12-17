package com.dajham.bankcore.application.service;

import com.dajham.bankcore.application.dto.TransferRequest;
import com.dajham.bankcore.application.dto.TransferResponse;
import com.dajham.bankcore.domain.model.Account;
import com.dajham.bankcore.domain.model.Transaction;
import com.dajham.bankcore.domain.port.AccountRepositoryPort;
import com.dajham.bankcore.domain.port.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TransferService.
 * Utiliza Mockito para aislar las dependencias y probar la lógica de
 * orquestación.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransferService - Application Service Tests")
class TransferServiceTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private TransferService transferService;

    private Account sourceAccount;
    private Account targetAccount;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        // Arrange: Configurar cuentas de prueba
        sourceAccount = new Account(
                1L,
                "ACC-SOURCE-123",
                new BigDecimal("1000.00"),
                100L);

        targetAccount = new Account(
                2L,
                "ACC-TARGET-456",
                new BigDecimal("500.00"),
                200L);

        // Request para transferir 300.00 de cuenta 1 a cuenta 2
        transferRequest = new TransferRequest(
                1L, // sourceAccountId
                2L, // targetAccountId
                new BigDecimal("300.00") // amount
        );
    }

    @Test
    @DisplayName("transfer() debe completarse exitosamente cuando el balance es suficiente")
    void transfer_ShouldSucceed_WhenBalanceIsSufficient() {
        // Arrange
        when(accountRepositoryPort.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepositoryPort.findById(2L)).thenReturn(Optional.of(targetAccount));

        Transaction mockTransaction = new Transaction(
                1L, // sourceAccountId
                2L, // targetAccountId
                new BigDecimal("300.00") // amount
        );
        mockTransaction.setId(999L);

        when(transactionRepositoryPort.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        TransferResponse response = transferService.transfer(transferRequest);

        // Assert
        assertNotNull(response, "La respuesta no debe ser null");
        assertEquals("SUCCESS", response.status(), "El estado debe ser SUCCESS");
        assertEquals(999L, response.transactionId(), "El ID de transacción debe coincidir");
        assertNotNull(response.referenceCode(), "Debe tener un código de referencia");

        // Verificar que los balances se actualizaron correctamente
        assertEquals(new BigDecimal("700.00"), sourceAccount.getBalance(),
                "El balance de la cuenta origen debe ser 700.00 (1000 - 300)");
        assertEquals(new BigDecimal("800.00"), targetAccount.getBalance(),
                "El balance de la cuenta destino debe ser 800.00 (500 + 300)");

        // Verificar que se guardaron ambas cuentas
        verify(accountRepositoryPort, times(2)).save(any(Account.class));
        verify(accountRepositoryPort).save(sourceAccount);
        verify(accountRepositoryPort).save(targetAccount);

        // Verificar que se guardó la transacción
        verify(transactionRepositoryPort, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("transfer() debe fallar cuando la cuenta origen no existe")
    void transfer_ShouldFail_WhenOriginAccountDoesNotExist() {
        // Arrange
        when(accountRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transferService.transfer(transferRequest),
                "Debe lanzar IllegalArgumentException cuando la cuenta origen no existe");

        assertEquals("La cuenta origen con ID 1 no existe", exception.getMessage(),
                "El mensaje debe indicar que la cuenta origen no existe");

        // Verificar que NO se intentó guardar nada
        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(transactionRepositoryPort, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("transfer() debe fallar cuando el balance es insuficiente")
    void transfer_ShouldFail_WhenBalanceIsInsufficient() {
        // Arrange
        // Crear cuenta origen con balance insuficiente
        Account poorAccount = new Account(
                1L,
                "ACC-POOR-123",
                new BigDecimal("100.00"), // Solo 100, pero se intenta transferir 300
                100L);

        when(accountRepositoryPort.findById(1L)).thenReturn(Optional.of(poorAccount));
        when(accountRepositoryPort.findById(2L)).thenReturn(Optional.of(targetAccount));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transferService.transfer(transferRequest),
                "Debe lanzar IllegalArgumentException cuando el balance es insuficiente");

        assertEquals("Saldo insuficiente para realizar el retiro", exception.getMessage(),
                "El mensaje debe indicar saldo insuficiente");

        // Verificar que NUNCA se llamó a save() para las cuentas
        verify(accountRepositoryPort, never()).save(any(Account.class));

        // Verificar que NUNCA se guardó la transacción
        verify(transactionRepositoryPort, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("transfer() debe fallar cuando origen y destino son la misma cuenta")
    void transfer_ShouldFail_WhenOriginAndTargetAreSame() {
        // Arrange
        TransferRequest sameAccountRequest = new TransferRequest(
                1L, // sourceAccountId
                1L, // targetAccountId (misma que origen)
                new BigDecimal("100.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transferService.transfer(sameAccountRequest),
                "Debe lanzar IllegalArgumentException cuando origen y destino son iguales");

        assertEquals("La cuenta origen y destino no pueden ser la misma", exception.getMessage());

        // Verificar que NO se consultaron cuentas
        verify(accountRepositoryPort, never()).findById(any());
        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(transactionRepositoryPort, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("transfer() debe fallar cuando la cuenta destino no existe")
    void transfer_ShouldFail_WhenTargetAccountDoesNotExist() {
        // Arrange
        when(accountRepositoryPort.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transferService.transfer(transferRequest),
                "Debe lanzar IllegalArgumentException cuando la cuenta destino no existe");

        assertEquals("La cuenta destino con ID 2 no existe", exception.getMessage(),
                "El mensaje debe indicar que la cuenta destino no existe");

        // Verificar que NO se guardó nada
        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(transactionRepositoryPort, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("transfer() debe crear transacción con los datos correctos")
    void transfer_ShouldCreateTransactionWithCorrectData() {
        // Arrange
        when(accountRepositoryPort.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepositoryPort.findById(2L)).thenReturn(Optional.of(targetAccount));

        Transaction mockTransaction = new Transaction(
                1L,
                2L,
                new BigDecimal("300.00"));
        mockTransaction.setId(888L);

        when(transactionRepositoryPort.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        transferService.transfer(transferRequest);

        // Assert: Verificar que se guardó una transacción con los datos correctos
        verify(transactionRepositoryPort).save(argThat(transaction -> transaction.getSourceAccountId().equals(1L) &&
                transaction.getTargetAccountId().equals(2L) &&
                transaction.getAmount().compareTo(new BigDecimal("300.00")) == 0));
    }
}
