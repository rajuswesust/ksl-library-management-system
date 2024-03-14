package com.konasl.bookservice.services;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.exceptions.CustomException;
import com.konasl.bookservice.payload.Message;
import com.konasl.bookservice.payload.WishlistRequest;

import java.util.List;

public interface BookService {
    Book addBook(Book book);
    List<Book> getBooksByTypeAndAvailability(String type, boolean isAvailable);

    Book getBookById(Long id) throws CustomException;

    Message updateBook(Long id, Book book) throws CustomException;
    Object addBookToWishlist(WishlistRequest wishlistRequest);
}
