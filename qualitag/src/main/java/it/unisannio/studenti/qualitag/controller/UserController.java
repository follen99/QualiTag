package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The UserController class is a REST controller that provides endpoints for managing users.
 */
@Log
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
   * Gets a user by its username.
   *
   * @param identifier The username OR the userId OR the email of the user to find.
   * @return The response entity.
   */
  @GetMapping("/user/{identifier}")
  public ResponseEntity<?> getUser(@PathVariable String identifier) {
    return userService.getUser(identifier);
  }

  /**
   * Gets the tags of a user by its username.
   *
   * @param username The username of the user to get the tags.
   * @return The response entity.
   */
  @GetMapping("/user/{username}/tags")
  public ResponseEntity<?> getUserTags(@PathVariable String username) {
    return userService.getUserTags(username);
  }

  /**
   * Modifies the user given the username.
   *
   * @param username The username of the user to get.
   * @return The response entity.
   */
  @PutMapping("/user/{username}")
  public ResponseEntity<?> updateUser(@RequestBody UserModifyDto userModifyDto,
      @PathVariable String username) {
    return userService.updateUser(userModifyDto, username);
  }

  /**
   * Updates the password of a user by its username.
   *
   * @param passwordUpdateDto The password update data.
   * @param username The username of the user to update the password.
   * @return The response entity.
   */
  @PutMapping("/user/{username}/password")
  public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto,
      @PathVariable String username) {
    return userService.updatePassword(passwordUpdateDto, username);
  }

  /**
   * Deletes a user by its username.
   *
   * @param username The username of the user to delete.
   * @return The response entity.
   */
  @DeleteMapping("/user/{username}")
  public ResponseEntity<?> deleteUser(@PathVariable String username) {
    return userService.deleteUser(username);
  }
}
