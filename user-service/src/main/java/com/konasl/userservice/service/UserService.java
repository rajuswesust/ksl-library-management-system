package com.konasl.userservice.service;

import com.fasterxml.jackson.databind.node.LongNode;
import com.konasl.userservice.payload.UserWishlistRequest;
import com.konasl.userservice.payload.WishlistRequest;
import com.konasl.userservice.exception.ExceptionClass;
import com.konasl.userservice.entity.User;
import com.konasl.userservice.payload.Message;
import com.konasl.userservice.payload.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserDto registerUser(User user) throws ExceptionClass;
    UserDto getUser(Long id) throws ExceptionClass;
    List<UserDto> getAllUser();
    Message deleteUser(Long id) throws ExceptionClass;

    Message updateUser(Long userId, UserDto userDto) throws ExceptionClass;

    ResponseEntity<String> addToWishlist(UserWishlistRequest wishlistRequest, Long userId) throws ExceptionClass;
}
