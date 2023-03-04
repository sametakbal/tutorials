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
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    public Integer getTeacherId(){
        if (teacher != null) {
            return teacher.getId();
        }
        return null;
    }

    @ManyToMany
    @JoinTable(
            name = "user_lectures",
            joinColumns = { @JoinColumn(name = "lecture_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }
    )
    private List<User> students;
}
