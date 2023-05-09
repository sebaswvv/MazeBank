package w.mazebank.models.requests;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountPatchRequest {
    @Min(value = 0, message = "Absolute limit must be greater than 0")
    private Double absoluteLimit;
}
