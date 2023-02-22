package com.sametakbal.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "lecture")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Lecture extends BaseEntity{
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToMany
    @JoinTable(
            name = "user_lectures",
            joinColumns = { @JoinColumn(name = "lecture_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }
    )
    private List<User> students;
}
