package com.konasl.bookservice.entity;

import jakarta.persistence.*;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "title cannot be blank")
    private String title;

    @NotBlank(message = "author cannot be blank")
    private String author;  //what if the book has multiple authors

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @Pattern(regexp = "\\d{4}", message = "Release year must be in yyyy format")
    @NotEmpty(message = "release year is required")
    private String releaseYear;

    @NotEmpty(message = "book-type is required")
    private String bookType;    //Genre or category

    @Min(value = 1, message = "Quantity must be greater than or equal to 0")
    private int quantity;

    @NotNull(message = "available copies is required")
    @Min(value = 0, message = "available copies must be greater than 0")
    private int availableCopies;

    @NotNull(message = "isAvailable is required")
    private boolean isAvailable;
}

