package com.konasl.bookservice.controllers;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.exceptions.CustomException;
import com.konasl.bookservice.payload.Message;
import com.konasl.bookservice.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    //add books
    //only admin access
    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            System.out.println("add book: " + book);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(book));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    //get books according to types
    //and get books by availability status
    @GetMapping
    public ResponseEntity<List<Book>> getBooksByType(@RequestParam(value = "type", required = false) String type,
                                                     @RequestParam(value = "available", required = false, defaultValue = "true") boolean isAvailable) {
        List<Book> books = bookService.getBooksByTypeAndAvailability(type, isAvailable);
        System.out.println("Type: "+type + " available: " + isAvailable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    //get a book details
    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable(name = "id") Long id) {
        try {
            return new ResponseEntity<>(bookService.getBookById(id), HttpStatus.OK);
        } catch (CustomException e) {
           return new ResponseEntity<>(e.getErrorMessage(), e.getStatus());
        }
    }

    //update book details
    //only admin
    @PutMapping("/{id}")
    public ResponseEntity<Message> updateBook(@PathVariable(name = "id") Long id,
                                              @RequestBody Book book) {
        try {
            System.out.println("book update: " + id + ", " + book);
            return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(id, book));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    //loan book to user
    //admin


    //return book by user

    //lost books records
    //return time overdue for a user, users
}
