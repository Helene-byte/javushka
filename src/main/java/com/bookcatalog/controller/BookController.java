package com.bookcatalog.controller;

import com.bookcatalog.config.AppProperties;
import com.bookcatalog.model.Book;
import com.bookcatalog.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/api")
public class BookController {
    private final BookService bookService;
    private final AppProperties appProperties;

    public BookController(BookService bookService, AppProperties appProperties) {
        this.bookService = bookService;
        this.appProperties = appProperties;
    }

    @GetMapping("/books")
    public String getAllBooks() {
        return "Books: " + bookService.getAllBooks();
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/app-info")
    public Map<String, String> getAppInfo() {
        return Map.of(
                "appName", appProperties.getName(),
                "appVersion", appProperties.getVersion(),
                "appDescription", appProperties.getDescription()
        );
    }
}
