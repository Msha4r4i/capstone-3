package com.example.substracker.Controller;

import com.example.substracker.API.ApiResponse;
import com.example.substracker.Model.User;
import com.example.substracker.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")

public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    @GetMapping("/get/dto")
    public ResponseEntity<?> getAllUsersDTOOut() {
        return ResponseEntity.status(200).body(userService.getAllUsersDTOOut());
    }

    @GetMapping("/get/{userId}/dto")
    public ResponseEntity<?> getUserDtoByUserId(@PathVariable Integer userId){
        return ResponseEntity.status(200).body(userService.getUserDTOOut(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user){
        userService.addUser(user);
        return ResponseEntity.status(200).body(new ApiResponse("User added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody User user) {
        userService.updateUser(id, user);
        return ResponseEntity.status(200).body(new ApiResponse("User updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.status(200).body(new ApiResponse("User deleted successfully"));
    }

    //Mshari
    @PutMapping("/{userId}/notifications/trigger")
    public ResponseEntity<?> trigger(@PathVariable Integer userId){
        userService.triggerNotifications(userId);
        return ResponseEntity.status(200).body(new ApiResponse("Notifications triggered successfully"));
    }

    // ✅ Get all subscribed users
    @GetMapping("/get-subscribed")
    public ResponseEntity<List<User>> getAllSubscribedUsers() {
        List<User> subscribedUsers = userService.getAllSubsUsers();
        return ResponseEntity.ok(subscribedUsers);
    }

    // ✅ Get all unsubscribed users
    @GetMapping("/get-unsubscribed")
    public ResponseEntity<List<User>> getAllUnsubscribedUsers() {
        List<User> unsubscribedUsers = userService.getAllUnsubsUsers();
        return ResponseEntity.ok(unsubscribedUsers);
    }
    //Mshari
    @GetMapping("/get/email/{email}")
    public ResponseEntity<?> existsBeEmail(@PathVariable String email){
        boolean exists =userService.existsByEmail(email);
        return ResponseEntity.status(200).body(exists);
    }
}
