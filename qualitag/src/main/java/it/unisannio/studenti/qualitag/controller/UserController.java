package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

  @GetMapping("/admin/users")
  public ResponseEntity<?> getAllUsers() {
    return userService.getAllUsers();
  }
}
