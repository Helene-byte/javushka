package com.bookcatalog.service;

import com.bookcatalog.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final List<Book> books = new ArrayList<>();
    public BookService() {
        books.add(new Book(1L, "Effective Java", "Joshua Bloch"));
        books.add(new Book(2L, "Spring in Action", "Craig Walls"));
        books.add(new Book(3L, "Clean Code", "Robert C. Martin"));
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public Optional<Book> getBookById(Long id) {
        return books.stream()
                .filter(book -> book.id().equals(id))
                .findFirst();
    }
}
