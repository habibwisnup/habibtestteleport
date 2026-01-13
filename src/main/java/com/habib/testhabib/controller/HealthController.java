package com.habib.testhabib.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HealthController {
    @GetMapping("/healthz")
    public String health() {
        return "OK";
    }
}