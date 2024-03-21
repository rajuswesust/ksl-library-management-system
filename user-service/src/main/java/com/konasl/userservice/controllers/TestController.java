package com.konasl.userservice.controllers;

import com.konasl.userservice.service.UserService;
import com.konasl.userservice.service.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    //@Autowired
    private UserService userService = new UserServiceImpl();
    @GetMapping
    public int test() {

        return userService.test();
    }
}
