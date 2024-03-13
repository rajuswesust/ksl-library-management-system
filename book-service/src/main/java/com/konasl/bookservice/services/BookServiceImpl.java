package com.konasl.bookservice.services;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.exceptions.CustomException;
import com.konasl.bookservice.payload.Message;
import com.konasl.bookservice.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService{

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book addBook(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn())) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    new Message("This book already exists!"));
        }
        book.setBookType(book.getBookType().toLowerCase());
        Book newBook = bookRepository.save(book);
        return newBook;
    }

    @Override
    public List<Book> getBooksByTypeAndAvailability(String type, boolean isAvailable) {
        List<Book> books;
        if (type != null && isAvailable) {
            books = bookRepository.findByBookTypeAndIsAvailable(type, true);
        } else if (type != null) {
            books = bookRepository.findByBookType(type);
        }
        else if(!isAvailable) {
            books = bookRepository.findByIsAvailable(false);
        }
        else {
            books = bookRepository.findByIsAvailable(true);
        }
        return books;
    }

    @Override
    public Book getBookById(Long id) throws CustomException {
        return bookRepository.findById(id).orElseThrow(()->new CustomException(HttpStatus.BAD_REQUEST,
                new Message("No Book with id: "+ id + " exists")));
    }

    @Override
    public Message updateBook(Long id, Book book) throws CustomException {
        Book existingBook = bookRepository.findById(id).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, new Message("Book does not exists")));
        BeanUtils.copyProperties(book, existingBook, "id");
        System.out.println("in service impl:(updating book):" + existingBook);
        bookRepository.save(existingBook);
        return new Message("Successfully updated!");
    }
}