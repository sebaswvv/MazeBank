package w.mazebank.models.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @NotNull(message = "Amount cannot be null")
    // @NotBlank(message = "Amount cannot be left empty")
    @Positive(message = "Amount should be a positive number")
    private double amount;

    private String description;

    @NotBlank(message = "Sender cannot be blank")
    @Pattern(regexp = "^NL[0-9]{2}[A-z0-9]{4}[0-9]{10}$", message = "Sender IBAN is not valid")
    private String senderIban;

    @NotBlank(message = "Receiver cannot be blank")
    @Pattern(regexp = "^NL[0-9]{2}[A-z0-9]{4}[0-9]{10}$", message = "Receiver IBAN is not valid")
    private String receiverIban;
}
