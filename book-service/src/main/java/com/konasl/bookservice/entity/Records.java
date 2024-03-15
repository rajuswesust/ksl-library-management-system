package com.konasl.bookservice.entity;

import com.konasl.bookservice.enums.BookRecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Records {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    Book book;

    private LocalDate lentDate;

    private LocalDate returnTime;

    private int duration;

    private BigDecimal fine;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookRecordStatus status;
}
