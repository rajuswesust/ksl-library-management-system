package com.konasl.bookservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserWishlistResponse {
    Long bookId;
    String title;
    String author;
    String isbn;
    String releaseYear;
    String bookType;
}
