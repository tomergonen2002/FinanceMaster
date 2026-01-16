package financemaster.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record TransactionDto(
    @NotBlank String description,
    @Positive Double amount,
    @NotBlank String type, // "INCOME" oder "EXPENSE"
    @NotNull Long categoryId,
    LocalDate date
) {}