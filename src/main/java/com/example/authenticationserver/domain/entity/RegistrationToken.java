package com.example.authenticationserver.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="RegistrationToken")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "token")
    private String token;
    @Column(name = "email")
    private String email;
    @Column(name = "expiration_date")
    private String expirationDate;

    @Column(name = "create_by")
    private Integer createBy;

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
