package com.mvnikitin.itracker.rest.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String first;
    @Column(name = "middle_name")
    private String middle;
    @Column(name = "last_name")
    private String last;
    @Column
    private String username;
    @Column
    private Long password;
    @Column
    private String phone;
    @Column
    private String email;
    @Column(name = "employee_code")
    private String code;
}
