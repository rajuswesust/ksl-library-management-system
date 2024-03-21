package com.konasl.bookservice.repository;

import com.konasl.bookservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<WishList, Long> {
    WishList findByBookId(Long bookId);

    WishList findByBookIdAndUserId(Long bookId, Long userId);
}
