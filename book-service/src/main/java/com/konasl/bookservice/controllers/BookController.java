package com.konasl.bookservice.controllers;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.exceptions.CustomException;
import com.konasl.bookservice.payload.LendReturnBookRequest;
import com.konasl.bookservice.payload.Message;
import com.konasl.bookservice.payload.WishlistRequest;
import com.konasl.bookservice.services.BookService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> addBook(@Valid @RequestBody Book book) {
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

    //add book to user's wishlist
    @PostMapping("/user-wishlist/add")
    public ResponseEntity<?> addUserWishlist(@RequestBody WishlistRequest wishlistRequest) {
        System.out.println(wishlistRequest);
        return ResponseEntity.ok(bookService.addBookToWishlist(wishlistRequest));
    }

    //remove a book from wishlist
    //add book to user's wishlist
    @PostMapping("/user-wishlist/remove")
    public ResponseEntity<?> removeBookFromWishlist(@RequestBody WishlistRequest wishlistRequest) {
        try {
            System.out.println("in book service: " + wishlistRequest);
            return ResponseEntity.ok(bookService.removeBookFromWishlist(wishlistRequest));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    //admin
    //lend a book to user
    //here id is the admins user' id
    @PostMapping("/lend")
    public ResponseEntity<?> lendBook(@RequestBody LendReturnBookRequest lendBookRequest) {
        System.out.println("lend books: " + lendBookRequest);
        try {
            return ResponseEntity.ok(bookService.lendBook(lendBookRequest));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    //return book
    //admin
    @PostMapping("/return-book")
    public ResponseEntity<?> returnBook(@RequestBody LendReturnBookRequest returnBookRequest) {
        System.out.println("return book: "+ returnBookRequest);
        try {
            return ResponseEntity.ok(bookService.returnBook(returnBookRequest));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    //get all records
    //get records of a user
    //can be filtered by userId, bookId
    //also by status
    @GetMapping("/records")
    public ResponseEntity<?> getRecords(@RequestParam(name = "userId", required = false) Long userId,
                                        @RequestParam(name = "bookId", required = false) Long bookId,
                                        @RequestParam(name = "status", required = false) String status) {
       try{
           System.out.println("get book record: " + userId+ ", " + bookId + ", " + status);
           return new ResponseEntity<>(bookService.getRecords(userId, bookId, status), HttpStatus.OK);
       }catch (CustomException e) {
           return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
       }
    }

    //what to do if a user lost a book?
    //can be reported while retuning the book
}
