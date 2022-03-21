package com.sametakbal.relationalcrud.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

@Entity
@Table(name = "page")
@Data
@EqualsAndHashCode
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

