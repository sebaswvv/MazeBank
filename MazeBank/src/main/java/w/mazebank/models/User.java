package w.mazebank.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import w.mazebank.enums.RoleType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private int bsn;

    private String firstName;

    private String lastName;

    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private RoleType role = RoleType.CUSTOMER;

    private LocalDate dateOfBirth;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private double dayLimit = 5000.00;

    @Builder.Default
    private double transactionLimit = 2000;

    private boolean blocked;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Account> accounts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !blocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public double getAmountRemaining() {
        double amountRemaining = dayLimit;
        if (accounts == null) return amountRemaining;
        for (Account account : accounts) {
            for (Transaction transaction : account.getSentTransactions()) {
                if (transaction.getTimestamp().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
                    amountRemaining -= transaction.getAmount();
                }
            }
        }
        return amountRemaining;
    }

}