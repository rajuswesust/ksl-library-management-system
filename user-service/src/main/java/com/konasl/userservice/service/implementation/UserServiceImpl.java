package com.konasl.userservice.service.implementation;

import com.konasl.userservice.payload.*;
import com.konasl.userservice.exception.ExceptionClass;
import com.konasl.userservice.entity.User;
import com.konasl.userservice.repository.UserRepository;
import com.konasl.userservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    private final String bookServiceURL = "http://localhost:8082/api/books";

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public UserDto registerUser(User user) throws ExceptionClass {
        if(userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            throw new ExceptionClass(HttpStatus.BAD_REQUEST,
                    new Message("User with the same username or email already exists"));
        }
        String currentDateTime = String.valueOf(LocalDateTime.now());
        user.setJoinedAt(currentDateTime);
        User newUser = userRepository.save(user);
        return toUserDto(user);
    }

    @Override
    public UserDto getUser(Long id) throws ExceptionClass{
        User user = userRepository.findById(id).orElseThrow(()->new ExceptionClass(HttpStatus.NOT_FOUND,
                new Message("User does not exists")));
        return toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserDto> list = new ArrayList<>();
        for (User x: users) {
            list.add(toUserDto(x));
        }
        return list;
    }

    @Override
    public Message deleteUser(Long id) throws ExceptionClass {
        if(!userRepository.existsById(id)) {
            throw new ExceptionClass(HttpStatus.NOT_FOUND, new Message("User does not exists"));
        }
        userRepository.deleteById(id);
        return new Message("User with id: " + id + ", deleted successfully");
    }

    @Override
    public Message updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId).orElseThrow(() ->
                new ExceptionClass(HttpStatus.BAD_REQUEST, new Message("User does not exists")));


        // Configure ModelMapper to ignore null properties
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // Map non-null fields from userDto to existingUser
        modelMapper.map(userDto, existingUser);

        // Save the updated user
        System.out.println("in service impl:(updating user):" + existingUser);
        userRepository.save(existingUser);
        return new Message("Successfully updated!");
    }

    @Override
    public ResponseEntity<String> addToWishlist(UserWishlistRequest userWishlistRequest, Long userId) throws ExceptionClass {
        if(!userRepository.existsById(userId)) {
            throw new ExceptionClass(HttpStatus.BAD_REQUEST, new Message("User with id : " + userId + " does not exists"));
        }
        String url = bookServiceURL + "/user-wishlist/add";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        WishlistRequest req = WishlistRequest.builder().bookId(userWishlistRequest.getBook_id()).userId(userId).
                build();
        HttpEntity<WishlistRequest> entity = new HttpEntity<>(req, headers);

        System.out.println("requesting to "+ url +": "+ req);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println(response);
        return response;
    }

    @Override
    public ResponseEntity<String> removeBookFromWishlist(UserWishlistRequest userWishlistRequest, Long userId) throws ExceptionClass {
        if(!userRepository.existsById(userId)) {
            throw new ExceptionClass(HttpStatus.BAD_REQUEST, new Message("User with id : " + userId + " does not exists"));
        }
        String url = bookServiceURL + "/user-wishlist/remove";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        WishlistRequest req = WishlistRequest.builder().bookId(userWishlistRequest.getBook_id()).userId(userId).
                build();
        HttpEntity<WishlistRequest> entity = new HttpEntity<>(req, headers);

        System.out.println("requesting to "+ url +": "+ req);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(response);
            return response;
        } catch (HttpClientErrorException e) {
            throw new ExceptionClass((HttpStatus) e.getStatusCode(), new Message("Error occurred while removing book from wishlist"));
        }
    }

    private UserDto toUserDto(User newUser) {
        UserDto newUserDto = new UserDto();
        newUserDto.setFirstName(newUser.getFirstName());
        newUserDto.setLastName(newUser.getLastName());
        newUserDto.setUsername(newUser.getUsername());
        newUserDto.setEmail(newUser.getEmail());
        newUserDto.setJoinedAt(newUser.getJoinedAt());
        newUserDto.setUserImage(newUser.getUserImage());
        newUserDto.setPhoneNumber(newUser.getPhoneNumber());
        return newUserDto;
    }
}
