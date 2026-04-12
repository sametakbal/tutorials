package com.saga.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stock")
public class InventoryController {

    @GetMapping
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "inventory-service");
    }
}
