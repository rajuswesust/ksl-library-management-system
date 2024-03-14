package com.konasl.bookservice.payload;

import lombok.Data;

@Data
public class WishlistRequest {
    Long userId;
    Long bookId;
}
