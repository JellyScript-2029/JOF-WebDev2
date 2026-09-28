package com.jeehli.webdev2;
 
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.stereotype.Repository;
 
@Repository
public class BookRepository {
    private final List<Book> books = new ArrayList<>();
 
    public BookRepository() {
        books.add(new Book("Clean Code", "Robert C. Martin", 1L));
        books.add(new Book("Effective Java", "Joshua Bloch", 2L));
        books.add(new Book("Domain-Driven Design", "Eric Evans", 3L));
        books.add(new Book("Systems Design Course", "Eric Evans", 4L));
    }
 
    public List<Book> getBooks() {
        return books;
    }
 
    public void addBooks(Book book) {
        books.add(book);
    }
}
 