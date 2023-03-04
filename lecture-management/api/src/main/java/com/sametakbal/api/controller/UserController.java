package com.sametakbal.api.controller;

import com.sametakbal.api.entity.User;
import com.sametakbal.api.entity.enums.Role;
import com.sametakbal.api.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    ResponseEntity<Page<User>> getUsers(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize ){
        return ResponseEntity.ok(userService.getAll(PageRequest.of(page,pageSize, Sort.by("id"))));
    }

    @PostMapping("/potential-students")
    ResponseEntity<List<User>> getPotentialStudents(@RequestBody List<Integer> studentIds){
        return ResponseEntity.ok(userService.getPotentialUsers(studentIds));
    }

    @GetMapping("/by-role")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam Role role){
        return ResponseEntity.ok(userService.getUserByRole(role));
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
