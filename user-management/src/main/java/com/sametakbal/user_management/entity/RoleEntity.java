package com.sametakbal.user_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
}
