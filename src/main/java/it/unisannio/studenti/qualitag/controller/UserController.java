package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The UserController class is a REST controller that provides endpoints for managing users.
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {
  private final UserService userService;

  /**
   * Constructs a new UserController.
   *
   * @param userService The user service.
   */
  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Registers a new user.
   *
   * @param userRegistrationDto The user registration DTO.
   * @return The response entity.
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
    return userService.registerUser(userRegistrationDto);
  }

  /**
   * Logs in a user.
   *
   * @param userLoginDto The user login DTO.
   * @return The response entity.
   */
  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userLoginDto) {
    return userService.loginUser(userLoginDto);
  }

  /**
   * Gets all users.
   *
   * @return The response entity.
   */
  @GetMapping("/admin/users")
  public ResponseEntity<?> getAllUsers() {
    return userService.getAllUsers();
  }

  /**
   * Gets a user by its username.
   *
   * @param username The username of the user to get.
   * @return The response entity.
   */
  @PutMapping("/user/{username}")
  public ResponseEntity<?> updateUser(@RequestBody UserModifyDto userModifyDto, @PathVariable String username) {
    return userService.updateUser(userModifyDto, username);
  }
}
