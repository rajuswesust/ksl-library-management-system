package com.konasl.userservice.controllers;

import com.konasl.userservice.payload.*;
import com.konasl.userservice.entity.User;
import com.konasl.userservice.service.UserService;
import com.konasl.userservice.exception.ExceptionClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUser() {
        return  ResponseEntity.ok(userService.getAllUser());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            System.out.println("new registering user: " + user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user));
        } catch (ExceptionClass e) {
            System.out.println(e);
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            UserDto userDto = userService.getUser(id);
            return ResponseEntity.ok(userDto);
        } catch (ExceptionClass e) {
            System.out.println(e);
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Message> deleteUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.deleteUser(id));
        } catch (ExceptionClass e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Message> updateUser(@PathVariable(name = "id") Long userId, @RequestBody UserDto userDto) {
       try {
           System.out.println("user update: " + userId + ", " + userDto);
           return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, userDto));
       } catch (ExceptionClass e) {
           return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
       }
    }

    //add a book to users wishlist
    @PostMapping("/{id}/wishlist")
    public ResponseEntity<?> addBookToWishlist(@RequestBody UserWishlistRequest wishlistRequest,
    @PathVariable(name = "id")Long userId) {
        try {
            return ResponseEntity.ok(userService.addToWishlist(wishlistRequest, userId));
        } catch (ExceptionClass e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    //remove a book from wishlist
    @PostMapping("/{id}/wishlist/remove-book")
    public ResponseEntity<?> removeBookFromWishlist(@RequestBody UserWishlistRequest wishlistRequest,
                                               @PathVariable(name = "id")Long userId) {
        try {
            return ResponseEntity.ok(userService.removeBookFromWishlist(wishlistRequest, userId));
        } catch (ExceptionClass e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrorMessage());
        }
    }

    //show users borrowed books
}
