package com.konasl.bookservice.services;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.entity.Records;
import com.konasl.bookservice.exceptions.CustomException;
import com.konasl.bookservice.payload.*;

import java.util.List;

public interface BookService {
    Book addBook(Book book);
    List<Book> getBooksByTypeAndAvailability(String type, boolean isAvailable);

    Book getBookById(Long id) throws CustomException;

    Message updateBook(Long id, Book book) throws CustomException;
    Object addBookToWishlist(WishlistRequest wishlistRequest) throws CustomException;
    Message removeBookFromWishlist(WishlistRequest wishlistRequest) throws CustomException;
    List<UserWishlistResponse> getWishlistByUser(Long userId) throws CustomException;
    Message lendBook(LendReturnBookRequest lendBookRequest) throws CustomException;
    BookReturnResponse returnBook(LendReturnBookRequest lendBookRequest) throws CustomException;
    List<Records> getRecords(Long userId, Long bookId, String status)  throws CustomException;
}
