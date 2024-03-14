package com.konasl.bookservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Define Many-to-One relationship with Book entity
    @ManyToOne
    @JoinColumn(name = "book_id") // This column will hold the foreign key to Book
    private Book book;

    @ElementCollection
    private List<Long> userIds;
}
