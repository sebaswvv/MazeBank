package w.mazebank.models.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPatchRequest {

    @Email
    @Nullable
    private String email;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number should be 10 digits")
    private String phoneNumber;

    private Double dayLimit;

    private Double transactionLimit;

    public String[] getFields() {
        return new String[]{"email", "firstName", "lastName", "phoneNumber", "dayLimit", "transactionLimit"};
    }
}
