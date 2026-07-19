package com.marwaneqada.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    String login() {
        return "security/login";
    }

    @GetMapping("/access-denied")
    String accessDenied() {
        return "security/access-denied";
    }
}
