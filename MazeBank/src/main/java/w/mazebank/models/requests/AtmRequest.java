package w.mazebank.models.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtmRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount should be a positive number")
    private Double amount;
}
