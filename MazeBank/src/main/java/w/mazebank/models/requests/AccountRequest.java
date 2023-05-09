package w.mazebank.models.requests;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import w.mazebank.enums.AccountType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @NotNull(message = "Account type is mandatory")
    @Enumerated(EnumType.ORDINAL)
    private AccountType accountType;

    @NotNull(message = "User is mandatory")
    private long userId;

    @NotNull(message = "Active status is mandatory")
    private boolean isActive; // will be named "active" in json request body

    @NotNull(message = "Absolute limit is mandatory")
    @Min(value = 0, message = "Absolute limit must be greater than 0")
    private double absoluteLimit;
}