package com.sametakbal.api.controller;

import com.sametakbal.api.entity.User;
import com.sametakbal.api.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }
    @GetMapping
    ResponseEntity<Page<User>> getUsers(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize ){
        return ResponseEntity.ok(userService.getAll(PageRequest.of(page,pageSize)));
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
