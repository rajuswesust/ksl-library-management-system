package com.konasl.bookservice.repository;

import com.konasl.bookservice.entity.Records;
import com.konasl.bookservice.enums.BookRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordsRepository extends JpaRepository<Records, Long> {
    //kam kore na!
    //List<Records> findByBookIdAndUserIdAndStatus(Long userId, Long bookId, BookRecordStatus status);

    @Query("SELECT r FROM Records r WHERE r.book.id = :bookId AND r.userId = :userId AND r.status = :status")
    List<Records> findByBookIdAndUserIdAndStatus(Long userId, Long bookId, BookRecordStatus status);
    List<Records> findAllByBookIdAndUserId(Long bookId, Long userId);
    List<Records> findAllByUserId(Long userId);
    List<Records> findAllByBookId(Long bookId);
    List<Records> findAllByStatus(BookRecordStatus status);


}
