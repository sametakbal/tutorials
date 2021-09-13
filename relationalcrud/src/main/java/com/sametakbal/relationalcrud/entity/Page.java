package com.sametakbal.relationalcrud.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "page")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Page {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "content")
    private String content;
    @ManyToOne
    @JoinColumn(name="book_id", nullable=false)
    private Book book;
}

