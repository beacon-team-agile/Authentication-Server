package com.example.authenticationserver.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="`User`")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String email;

    @Column(name = "`password`")
    private String password;
    private boolean status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "userId")
    private List<UserRole> userRoles;

    @OneToOne(fetch = FetchType.EAGER)
    private RegistrationToken registrationToken;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", permission=" + userRoles +
                ", permission=" + registrationToken +
                '}';
    }
}
