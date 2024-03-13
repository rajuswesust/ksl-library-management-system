package com.konasl.userservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto{
    String firstName;
    String lastName;
    String username;
    String email;
    String phoneNumber;
    String joinedAt;
    String userImage;
}
