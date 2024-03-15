package com.konasl.bookservice.repository;

import com.konasl.bookservice.entity.Records;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordsRepository extends JpaRepository<Records, Long> {
    List<Records> findAllByBookIdAndUserId(Long bookId, Long userId);
}
