package com.mvnikitin.itracker.rest.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "projects")
public class ProjectEntity {

    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "biz_unit")
    private String unit;

    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private UserEntity admin;

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserEntity owner;
}
