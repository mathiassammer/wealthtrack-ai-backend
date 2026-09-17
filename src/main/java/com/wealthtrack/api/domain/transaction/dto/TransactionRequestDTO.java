package com.wealthtrack.api.domain.transaction.dto;

import com.wealthtrack.api.domain.transaction.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// O DTO que o Front-End vai nos enviar
public record TransactionRequestDTO(
        @NotNull UUID accountId,
        @NotNull @Positive BigDecimal amount, // Garante que não manda valor negativo
        @NotNull TransactionType type,
        @NotNull String category,
        String description,
        @NotNull LocalDate date
) {}