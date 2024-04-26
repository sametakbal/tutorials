package com.sametakbal.aws.deployment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @GetMapping
    public ResponseEntity<String> getMessage() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/say")
    public ResponseEntity<String> say(@RequestParam String name) {
        return ResponseEntity.ok(String.format("Hello %s!", name));
    }
}
