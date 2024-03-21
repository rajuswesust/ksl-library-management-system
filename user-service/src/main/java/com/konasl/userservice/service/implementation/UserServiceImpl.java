package com.konasl.userservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konasl.userservice.payload.*;
import com.konasl.userservice.exception.ExceptionClass;
import com.konasl.userservice.entity.User;
import com.konasl.userservice.repository.UserRepository;
import com.konasl.userservice.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@NoArgsConstructor
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    private int testVal = 0;

    @Override
    public int test() {
        System.out.println(++testVal);
        return testVal;
    }

    @Autowired
    private RestTemplate restTemplate;

    private final String bookServiceURL = "http://localhost:8082/api/books";

//    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
//        this.userRepository = userRepository;
//        this.modelMapper = modelMapper;
//    }


    @Override
    public UserDto registerUser(User user) throws ExceptionClass {
        if(userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            throw new ExceptionClass(HttpStatus.BAD_REQUEST,
                    new Message("User with the same username or email already exists"));
        }
//        String currentDateTime = String.valueOf(LocalDateTime.now());
//        user.setJoinedAt(currentDateTime);
        user.setJoinedAt(LocalDateTime.now());
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
    public  Message addToWishlist(UserWishlistRequest userWishlistRequest, Long userId) throws ExceptionClass {
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
        try {
            ResponseEntity<Message> response = restTemplate.exchange(url, HttpMethod.POST, entity, Message.class);
            System.out.println(response.getBody());
            return response.getBody();
         } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            throw getResponseForError(e);
        }
    }

    @Override
    public Message removeBookFromWishlist(UserWishlistRequest userWishlistRequest, Long userId) throws ExceptionClass {
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
            ResponseEntity<Message> response = restTemplate.exchange(url, HttpMethod.POST, entity, Message.class);
            System.out.println(response);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw getResponseForError(e);
        }
    }

    private UserDto toUserDto(User newUser) {
        UserDto newUserDto = new UserDto();
        newUserDto.setFirstName(newUser.getFirstName());
        newUserDto.setLastName(newUser.getLastName());
        newUserDto.setUsername(newUser.getUsername());
        newUserDto.setEmail(newUser.getEmail());
        newUserDto.setJoinedAt(String.valueOf(newUser.getJoinedAt()));
        newUserDto.setUserImage(newUser.getUserImage());
        newUserDto.setPhoneNumber(newUser.getPhoneNumber());
        return newUserDto;
    }
    private ExceptionClass getResponseForError(HttpClientErrorException e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = null;
        try {
            jsonResponse = objectMapper.readTree(e.getResponseBodyAs(String.class));
        } catch (JsonProcessingException ex) {
            return new ExceptionClass(HttpStatus.INTERNAL_SERVER_ERROR, new Message("Something went wrong!"));
        }
        String messageContent = jsonResponse.get("message").asText();
        return new ExceptionClass((HttpStatus) e.getStatusCode(), new Message(messageContent));
    }
}
