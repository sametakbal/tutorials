package com.sametakbal.relationalcrud.controller;

import com.sametakbal.relationalcrud.entity.Author;
import com.sametakbal.relationalcrud.service.IAuthorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/authors")
@AllArgsConstructor
public class AuthorController {

    private final IAuthorService service;

    @PostMapping
    public ResponseEntity<Void> saveAuthor(@RequestBody Author author){
        service.createAuthor(author);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
