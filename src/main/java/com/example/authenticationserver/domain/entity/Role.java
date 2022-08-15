package com.example.authenticationserver.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="`Role`")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_name")
    private String roleName;
    @Column(name = "role_description")
    private String roleDescription;
    @Column(name = "create_date")
    private String createDate;
    @Column(name = "last_modification_date")
    private String lastModificationDate;
}
