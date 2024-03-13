package com.konasl.bookservice.repository;

import com.konasl.bookservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    List<Book> findByBookType(String type);

    // Retrieve books by type and availability
    List<Book> findByBookTypeAndIsAvailable(String type, boolean isAvailable);

    // Retrieve books by availability
    List<Book> findByIsAvailable(boolean isAvailable);
}
