package w.mazebank.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullUserResponse {
    private long id;
    private String email;
    private int bsn;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
    private String dateOfBirth;
    private String createdAt;
    private double dayLimit;
    private double transactionLimit;
    private double amountRemaining;
    private boolean blocked;
}
