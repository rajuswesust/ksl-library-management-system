package com.konasl.userservice.service;

import com.konasl.userservice.payload.WishlistRequest;
import com.konasl.userservice.exception.ExceptionClass;
import com.konasl.userservice.entity.User;
import com.konasl.userservice.payload.Message;
import com.konasl.userservice.payload.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(User user) throws ExceptionClass;
    UserDto getUser(Long id) throws ExceptionClass;
    List<UserDto> getAllUser();
    Message deleteUser(Long id) throws ExceptionClass;

    Message updateUser(Long userId, UserDto userDto) throws ExceptionClass;

    Message addToWishlist(WishlistRequest wishlistRequest);
}