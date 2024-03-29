package com.konasl.bookservice.services;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.entity.Records;
import com.konasl.bookservice.entity.WishList;
import com.konasl.bookservice.enums.BookRecordStatus;
import com.konasl.bookservice.exceptions.CustomException;
import com.konasl.bookservice.payload.*;
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
import java.util.ArrayList;
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
    public Message addBookToWishlist(WishlistRequest wishlistRequest) throws CustomException{
        System.out.println("book service: "+ wishlistRequest);

        // Retrieve the Book and Check if it exists
        Book book = bookRepository.findById(wishlistRequest.getBookId()).orElseThrow(
                ()->   new CustomException(HttpStatus.NOT_FOUND, new Message("Book with id: " + wishlistRequest.getBookId() + " does not exists"))
        );
        Long userId = wishlistRequest.getUserId();
        WishList existingRecord = wishlistRepository.findByBookIdAndUserId(book.getId(), userId);
        WishList newRecord = WishList.builder().book(book).userId(userId).build();
        if(existingRecord != null) {
            throw new CustomException(HttpStatus.CONFLICT, new Message("This book is already in the wishlist"));
        }
        wishlistRepository.save(newRecord);
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

        WishList existingRecord = wishlistRepository.findByBookIdAndUserId(book.getId(), userId);
        if(existingRecord == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, new Message("Book with id: " + bookId + " is not in users wishlist"));
        }
        wishlistRepository.delete(existingRecord);
        return new Message(book.getTitle() + ", is removed from wishlist!");
    }

    @Override
    public List<UserWishlistResponse> getWishlistByUser(Long userId) throws CustomException {
        List<WishList> list = wishlistRepository.findAllByUserId(userId);
        List<UserWishlistResponse> userWishlist = new ArrayList<>();
        list.forEach(it -> {
            System.out.println(it);
            Book curr = it.getBook();
            userWishlist.add(UserWishlistResponse.builder().bookId(curr.getId()).title(curr.getTitle()).bookType(curr.getBookType())
                    .isbn(curr.getIsbn()).author(curr.getAuthor()).releaseYear(curr.getReleaseYear()).build());
        });
        return userWishlist;
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
            Boolean is_lost = lendBookRequest.getInfo().getIs_lost();

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
            BookRecordStatus bookRecordStatus = is_lost ? BookRecordStatus.LOST : BookRecordStatus.RETURNED;
            filteredRecords.get(0).setReturnTime(LocalDate.now());
            filteredRecords.get(0).setStatus(bookRecordStatus);
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

    @Override
    public List<Records> getRecords(Long userId, Long bookId, String status) throws CustomException{
        BookRecordStatus bookRecordStatus = null;
        if(status != null && status.equals("due")) {
            bookRecordStatus = BookRecordStatus.DUE;
        } else if(status != null && status.equals("returned")) {
            bookRecordStatus = BookRecordStatus.RETURNED;
        } else if(status != null && status.equals("lost")) {
            bookRecordStatus = BookRecordStatus.LOST;
        } else if (status != null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, new Message("No such status as " + status));
        }
        List<Records> records;
        if(userId != null && bookId != null && bookRecordStatus != null) {
           records =  recordsRepository.findByBookIdAndUserIdAndStatus(userId, bookId, bookRecordStatus);
        } else if(userId != null) {
            records = recordsRepository.findAllByUserId(userId);
        } else if(bookId != null) {
            records = recordsRepository.findAllByBookId(bookId);
        } else if(status != null){
            records = recordsRepository.findAllByStatus(bookRecordStatus);
        } else {
            records = recordsRepository.findAll();
        }
        return records;
    }

}
