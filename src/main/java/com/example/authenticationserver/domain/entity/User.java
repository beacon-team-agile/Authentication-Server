package com.example.authenticationserver.domain.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="`User`")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String userName;

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
                ", username='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", permission=" + userRoles +
                ", permission=" + registrationToken +
                '}';
    }
}
