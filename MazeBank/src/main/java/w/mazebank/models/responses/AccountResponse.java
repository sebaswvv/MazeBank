package w.mazebank.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AccountResponse {
    private Long id;
    private String iban;
    private double balance;
    private LocalDateTime createdAt;

}
