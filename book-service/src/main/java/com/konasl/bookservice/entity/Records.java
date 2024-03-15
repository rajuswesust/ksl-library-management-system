package com.konasl.bookservice.entity;

import com.konasl.bookservice.enums.BookRecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
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

    public void calculateFine() {
        BigDecimal finePerDay = BigDecimal.valueOf(10); // 10 taka
        int overdueDays;
        if (returnTime == null) {
            overdueDays = (int) ChronoUnit.DAYS.between(lentDate, LocalDate.now());
        } else {
            overdueDays = (int) ChronoUnit.DAYS.between(lentDate, returnTime);
        }
        System.out.println(overdueDays + ", days");
        if (overdueDays > 7) {
            fine = finePerDay.multiply(BigDecimal.valueOf(overdueDays));
        } else {
            fine = BigDecimal.ZERO;
        }
    }
}
