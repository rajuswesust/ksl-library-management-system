package com.konasl.bookservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LendBookRequest {
    Long admin_id;
    UserLendBookRequest lend;
}
