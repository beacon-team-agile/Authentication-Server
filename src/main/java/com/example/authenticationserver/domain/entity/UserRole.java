package com.example.authenticationserver.domain.entity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="`UserRole`")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId; //id from User table
    @Column(name = "role_id")
    private Integer roleId; //role from Role table
    @Column(name = "active_flag")
    private Boolean activeFlag;
    @Column(name = "create_date")
    private String createDate;
    @Column(name = "last_modification_date")
    private String lastModificationDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Role roleEntity;

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleId=" + roleId +
                ", activeFlag=" + activeFlag +
                ", createDate='" + createDate + '\'' +
                ", lastModificationDate='" + lastModificationDate + '\'' +
                '}';
    }
}
