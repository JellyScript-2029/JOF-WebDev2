package com.jeehli.webdev2;
 
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
 
@Controller
@RequestMapping("/books")
public class BookController {
 
    private final BookService bookService;
 
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
 
    @GetMapping
    @ResponseBody
    public List<Book> getAllBooks(@RequestParam(required = false) String author) {
        if (author == null) {
            return bookService.returnAllBooks();
        }
        List<Book> filteredBooks = new ArrayList<>();
        for (Book b : bookService.returnAllBooks()) {
            if (b.getAuthor().equalsIgnoreCase(author)) {
                filteredBooks.add(b);
            }
        }
        return filteredBooks;
    }
 
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        model.addAttribute("book", book);
        return ResponseEntity.ok(book);
    }
 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createBook(@RequestParam String title,
                             @RequestParam String author,
                             @RequestParam Long bookId) {
        bookService.addBook(new Book(title, author, bookId));
        return "redirect:/books";
    }
}
