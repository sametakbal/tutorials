package com.sametakbal.api.controller;

import com.sametakbal.api.entity.User;
import com.sametakbal.api.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }
    @GetMapping
    ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(userService.getAll());
    }
    @GetMapping("/{id}")
    ResponseEntity<User> getUserById(@PathVariable Integer id){
        return ResponseEntity.ok(userService.getById(id));
    }
    @PostMapping
    ResponseEntity<User> createUser(@RequestBody User user){
        return ResponseEntity.ok(userService.save(user));
    }
    @DeleteMapping("/{id}")
    ResponseEntity<Void> getDeleteUserById(@PathVariable Integer id){
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
