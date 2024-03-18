package com.konasl.bookservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LendReturnDto {
    Long user_id;
    Long book_id;
    Boolean is_lost;
}
