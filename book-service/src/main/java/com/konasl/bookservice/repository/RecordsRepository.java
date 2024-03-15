package com.konasl.bookservice.repository;

import com.konasl.bookservice.entity.Records;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordsRepository extends JpaRepository<Records, Long> {
    Records findByBookId(Long bookId);
}
