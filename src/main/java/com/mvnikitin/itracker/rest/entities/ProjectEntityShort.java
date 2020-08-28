package com.mvnikitin.itracker.rest.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "projects")
public class ProjectEntityShort {

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
    @Column(name = "admin_id")
    private long adminId;
    @Column(name = "owner_id")
    private long ownerId;
}
