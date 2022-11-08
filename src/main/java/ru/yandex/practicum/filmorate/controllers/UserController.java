package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Operations with Users")
public class UserController {
    UserService userService;
    EventService eventService;

    @Autowired
    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping
    @Operation(summary = "returns all users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "returns user by id if exists")
    public User findById(@PathVariable Integer id) {
        return userService.findUserById(id);
    }

    @PostMapping
    @Operation(summary = "creates new user")
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    @Operation(summary = "updates user if exists")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @Operation(summary = "adds new friend to users friends-list")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @Operation(summary = "deletes friend from users friend-list if exists")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @Operation(summary = "returns users friends-list")
    public List<User> getFriendsSet(@PathVariable Integer id) {
        return userService.getFriendsSet(id);
    }

    @GetMapping("/{id}/friends/common/{friendsId}")
    @Operation(summary = "returns mutual friends between another user")
    public List<User> getMutualFriendsSet(@PathVariable Integer id, @PathVariable Integer friendsId) {
        return userService.getMutualFriendsSet(id, friendsId);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "deletes user by id if exists")
    public void deleteUserById(@PathVariable Integer userId) {
        userService.deleteUserById(userId);
    }

    @GetMapping("/{id}/feed")
    @Operation(summary = "returns users events-feed")
    public List<Event> getFeedByUserId(@PathVariable int id){
        return eventService.getFeedByUserId(id);
    }
}
