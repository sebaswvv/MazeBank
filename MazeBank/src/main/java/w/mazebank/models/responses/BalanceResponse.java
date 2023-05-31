package w.mazebank.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    private Long userId;
    private double checkingBalance;
    private double savingsBalance;
    private double totalBalance;
}
