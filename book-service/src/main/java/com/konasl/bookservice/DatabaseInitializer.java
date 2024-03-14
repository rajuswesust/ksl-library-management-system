package com.konasl.bookservice;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDatabase();
    }

    private void initializeDatabase() {
        // Insert books into the database
        Book book1 = Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("9780743273565")
                .releaseYear("1925")
                .bookType("fiction")
                .quantity(10)
                .availableCopies(10)
                .isAvailable(true)
                .build();
        bookRepository.save(book1);

        Book book2 = Book.builder()
                .title("To Kill a Mockingbird")
                .author("Harper Lee")
                .isbn("9780061120084")
                .releaseYear("1960")
                .bookType("fiction")
                .quantity(8)
                .availableCopies(8)
                .isAvailable(true)
                .build();
        bookRepository.save(book2);


        Book book3 = Book.builder()
                .title("Pride and Prejudice")
                .author("Jane Austen")
                .isbn("9780141199078")
                .releaseYear("1813")
                .bookType("romance")
                .quantity(7)
                .availableCopies(7)
                .isAvailable(true)
                .build();
        bookRepository.save(book3);


        Book book4 = Book.builder()
                .title("The Lord of the Rings")
                .author("J.R.R. Tolkien")
                .isbn("9780618574940")
                .releaseYear("1954")
                .bookType("fantasy")
                .quantity(11)
                .availableCopies(11)
                .isAvailable(false)
                .build();
        bookRepository.save(book4);

        Book book5 = Book.builder()
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .isbn("9781408855652")
                .releaseYear("1997")
                .bookType("fantasy")
                .quantity(5)
                .availableCopies(5)
                .isAvailable(false)
                .build();
        bookRepository.save(book5);
    }
}

