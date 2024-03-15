package com.konasl.userservice.service;

import com.konasl.userservice.payload.*;
import com.konasl.userservice.exception.ExceptionClass;
import com.konasl.userservice.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserDto registerUser(User user) throws ExceptionClass;
    UserDto getUser(Long id) throws ExceptionClass;
    List<UserDto> getAllUser();
    Message deleteUser(Long id) throws ExceptionClass;

    Message updateUser(Long userId, UserDto userDto) throws ExceptionClass;

    ResponseEntity<String> addToWishlist(UserWishlistRequest wishlistRequest, Long userId) throws ExceptionClass;
    ResponseEntity<String> removeBookFromWishlist(UserWishlistRequest wishlistRequest, Long userId) throws ExceptionClass;

}
