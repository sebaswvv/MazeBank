package w.mazebank.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import w.mazebank.enums.AccountType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {
    private long id;
    private int accountType;
    private String iban;
    private UserResponse user;
    private double balance;

    @Builder.Default
    private Double absoluteLimit = null;

    @Builder.Default
    private Boolean active = null;

    private LocalDateTime createdAt;

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType.getValue();
    }
}
