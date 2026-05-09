package com.example.rest.controller;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RootRestControllerTest {

    private final RootRestController controller = new RootRestController();

    @Test
    void rootReturnsExpectedServiceLinks() {
        LinkedHashMap<String, Object> response = controller.root();

        assertEquals("finance-tracker-rest is running", response.get("message"));
        assertEquals("http://localhost:8084/api/main", response.get("main"));
        assertEquals("http://localhost:8084/api/users", response.get("users"));
        assertEquals("http://localhost:8084/api/transactions/user/1", response.get("transactions"));
    }
}
