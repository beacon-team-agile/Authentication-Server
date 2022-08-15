package com.example.authenticationserver.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="RegistrationToken")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "token")
    private String token;
    @Column(name = "email")
    private String email;
    @Column(name = "expiration_date")
    private String expirationDate;

    @OneToOne
    @JoinColumn(name = "create_by_user")
    private User createBy;

    @Override
    public String toString() {
        return "RegistrationToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                '}';
    }
}
