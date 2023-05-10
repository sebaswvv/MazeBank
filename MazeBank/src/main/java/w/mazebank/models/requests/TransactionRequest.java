package w.mazebank.models.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @Min(value = 0, message = "Amount should be positive")
    private double amount;

    private String description;

    @NotBlank(message = "Sender cannot be blank")
    @Pattern(regexp = "^NL[0-9]{2}[A-z0-9]{4}[0-9]{10}$", message = "Sender IBAN is not valid")
    private String senderIban;

    @NotBlank(message = "Receiver cannot be blank")
    @Pattern(regexp = "^NL[0-9]{2}[A-z0-9]{4}[0-9]{10}$", message = "Receiver IBAN is not valid")
    private String receiverIban;
}
