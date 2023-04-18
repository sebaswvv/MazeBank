package W.MazeBank.models;

import W.MazeBank.enums.RoleType;

import java.time.LocalDate;

public class User{

    private long id;
    private int bsn;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private RoleType role;
    private LocalDate dateOfBirth;
    private LocalDate createdAt;
}
