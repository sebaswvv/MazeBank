package w.mazebank.models.requests;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountPatchRequest {
    @Max(value = 0, message = "Absolute limit must be less than or equal to 0")
    private Double absoluteLimit;
}
