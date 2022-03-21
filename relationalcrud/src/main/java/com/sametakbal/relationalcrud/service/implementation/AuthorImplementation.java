package com.sametakbal.relationalcrud.service.implementation;

import com.sametakbal.relationalcrud.entity.Author;
import com.sametakbal.relationalcrud.repository.IAuthorRepository;
import com.sametakbal.relationalcrud.service.IAuthorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorImplementation implements IAuthorService {
    private final IAuthorRepository repo;

    @Override
    public void createAuthor(Author author) {
        repo.save(author);
    }
}
