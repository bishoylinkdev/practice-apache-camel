package com.example.practiceapachecamel.routes;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Getter
    @Setter
    static class User {
        private String name;
        private String emailAddress;
        private String mobileNumber;
    }

    public Controller() {
    }

    @GetMapping("/home")
    public ResponseEntity<User> homePage() {
        User user = new User();
        user.setName("bishoy");
        user.setEmailAddress("bishoy@mail.com");
        user.setMobileNumber("071123-12345-7890");
        return ResponseEntity.ok(user);
    }

    @PostMapping("/producer")
    public void produce() {
    }

}
