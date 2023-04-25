package w.mazebank.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private int bsn;
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}