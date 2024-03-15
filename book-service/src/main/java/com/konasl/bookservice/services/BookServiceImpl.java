package com.konasl.bookservice.services;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.entity.Records;
import com.konasl.bookservice.entity.WishList;
import com.konasl.bookservice.enums.BookRecordStatus;
import com.konasl.bookservice.exceptions.CustomException;
import com.konasl.bookservice.payload.BookReturnResponse;
import com.konasl.bookservice.payload.LendReturnBookRequest;
import com.konasl.bookservice.payload.Message;
import com.konasl.bookservice.payload.WishlistRequest;
import com.konasl.bookservice.repository.BookRepository;
import com.konasl.bookservice.repository.RecordsRepository;
import com.konasl.bookservice.repository.WishlistRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService{

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private RecordsRepository recordsRepository;

    @Autowired
    private RestTemplate restTemplate;
    private final String userServiceUrl = "http://localhost:8081/api/users";

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

    @Override
    public Object addBookToWishlist(WishlistRequest wishlistRequest) throws CustomException{
        System.out.println("book service: "+ wishlistRequest);

        // Retrieve the Book object by its ID
        Book book = bookRepository.findById(wishlistRequest.getBookId()).orElseThrow(
                ()->   new CustomException(HttpStatus.NOT_FOUND, new Message("Book with id: " + wishlistRequest.getBookId() + " does not exists"))
        );

        WishList existingList = wishlistRepository.findByBookId(book.getId());
        WishList newList = WishList.builder().book(book).build();
        if(existingList == null) {
            newList.setUserIds(Collections.singletonList(wishlistRequest.getUserId()));
            wishlistRepository.save(newList);
        }
        else  {
            existingList.getUserIds().add(wishlistRequest.getUserId());
            wishlistRepository.save(existingList);
        }
        return new Message(book.getTitle() + ", is added to the wishlist!");
    }

    @Override
    public Message removeBookFromWishlist(WishlistRequest wishlistRequest) throws CustomException{
        Long userId = wishlistRequest.getUserId();
        Long bookId = wishlistRequest.getBookId();

        // Retrieve the Book object by its ID
        Book book = bookRepository.findById(bookId).orElseThrow(
                ()->   new CustomException(HttpStatus.NOT_FOUND, new Message("Book with id: " + bookId + " does not exists"))
        );

        WishList existingList = wishlistRepository.findByBookId(book.getId());
        if(existingList == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, new Message("Book with id: " + bookId + " is not in users wishlist"));
        }
        existingList.getUserIds().remove(userId);
        wishlistRepository.save(existingList);
        return new Message(book.getTitle() + ", is removed from wishlist!");
    }

    @Override
    public Message lendBook(LendReturnBookRequest lendBookRequest) throws CustomException{
        try {
            Long adminId = lendBookRequest.getAdmin_id();
            Long userId = lendBookRequest.getInfo().getUser_id();
            Long bookId = lendBookRequest.getInfo().getBook_id();

            // Check if the admin exists
            String adminUrl = userServiceUrl + "/" + adminId;
            restTemplate.getForEntity(adminUrl, String.class);

            // Check if the user exists
            String userUrl = userServiceUrl + "/" + userId;
            restTemplate.getForEntity(userUrl, String.class);

            //book exists?
            Book book = bookRepository.findById(bookId).orElseThrow(()->
                    new CustomException(HttpStatus.NOT_FOUND, new Message("Book with id: " + bookId + " does not exists")));

            //any copies available?
            if(book.getAvailableCopies() == 0) {
                throw new CustomException(HttpStatus.BAD_REQUEST, new Message("No copies available"));
            }

            //if the user already borrowed the book
            List<Records> records = recordsRepository.findAllByBookIdAndUserId(bookId, userId);
            List<Records> filteredRecords = records.stream()
                    .filter(record -> record.getReturnTime() == null
                            && record.getStatus() == BookRecordStatus.DUE)
                    .toList();
            if(!filteredRecords.isEmpty()) {
                throw new CustomException(HttpStatus.FORBIDDEN, new Message("User Already borrowed this book"));
            }
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);

            //save as record
            Records x = Records.builder().book(book).lentDate(LocalDate.now()).status(BookRecordStatus.DUE).userId(userId).build();
            recordsRepository.save(x);
            return new Message("Book lent successfully");
        } catch (HttpClientErrorException.NotFound e) {
            throw new CustomException(HttpStatus.NOT_FOUND, new Message("User or admin does not exist"));
        }
    }

    @Override
    public BookReturnResponse returnBook(LendReturnBookRequest lendBookRequest) throws CustomException {
        try {
            Long adminId = lendBookRequest.getAdmin_id();
            Long userId = lendBookRequest.getInfo().getUser_id();
            Long bookId = lendBookRequest.getInfo().getBook_id();

            // Check if the admin exists
            String adminUrl = userServiceUrl + "/" + adminId;
            restTemplate.getForEntity(adminUrl, String.class);

            // Check if the user exists
            String userUrl = userServiceUrl + "/" + userId;
            restTemplate.getForEntity(userUrl, String.class);

            //book exists?
            Book book = bookRepository.findById(bookId).orElseThrow(()->
                    new CustomException(HttpStatus.NOT_FOUND, new Message("Book with id: " + bookId + " does not exists")));

            //any copies available?
            if(book.getAvailableCopies() == book.getQuantity()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, new Message("All the copies has been returned already"));
            }

            //update the record
            List<Records> records = recordsRepository.findAllByBookIdAndUserId(bookId, userId);
            List<Records> filteredRecords = records.stream()
                    .filter(record -> record.getReturnTime() == null
                            && record.getStatus() == BookRecordStatus.DUE)
                    .toList();

            if(filteredRecords.size() != 1) {
                throw new CustomException(HttpStatus.BAD_REQUEST, new Message("No such previous record found"));
            }
            filteredRecords.get(0).setReturnTime(LocalDate.now());
            filteredRecords.get(0).setStatus(BookRecordStatus.RETURNED);
            filteredRecords.get(0).calculateFine();
            recordsRepository.save(filteredRecords.get(0));

            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
            return BookReturnResponse.builder().message(new Message("Book returned successfully"))
                    .fine(filteredRecords.get(0).getFine())
                    .build();
        } catch (HttpClientErrorException.NotFound e) {
            throw new CustomException(HttpStatus.NOT_FOUND, new Message("User or admin does not exist"));
        }
    }


}
