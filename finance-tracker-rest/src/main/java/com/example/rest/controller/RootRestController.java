package com.example.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
public class RootRestController {

    @GetMapping("/")
    public LinkedHashMap<String, Object> root() {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("message", "finance-tracker-rest is running");
        response.put("main", "http://localhost:8084/api/main");
        response.put("users", "http://localhost:8084/api/users");
        response.put("transactions", "http://localhost:8084/api/transactions/user/1");
        return response;
    }
}
