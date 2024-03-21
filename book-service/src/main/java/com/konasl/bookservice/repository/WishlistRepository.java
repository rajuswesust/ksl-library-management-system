package com.konasl.bookservice.repository;

import com.konasl.bookservice.entity.Book;
import com.konasl.bookservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<WishList, Long> {
    WishList findByBookId(Long bookId);

    WishList findByBookIdAndUserId(Long bookId, Long userId);

    List<WishList> findAllByUserId(Long userId);
}
