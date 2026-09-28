package com.jeehli.webdev2;

import java.util.List;

import org.springframework.stereotype.Service;
@Service 

public class BookService {
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> returnAllBooks () {
        return bookRepository.getBooks();
    }

    public Book getBookById (Long id) {
        return bookRepository.getBooks().stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }

    public void addBook (Book book) {
        bookRepository.addBooks(book);
    }
}