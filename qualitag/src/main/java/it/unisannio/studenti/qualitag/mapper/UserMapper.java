package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.service.UserService;

/**
 * The UserMapper class provides methods to convert between User entities and User DTOs.
 */
public class UserMapper {

  private final UserService userService;

  /**
   * Constructs a new UserMapper.
   *
   * @param userService The user service.
   */
  public UserMapper(UserService userService) {
    this.userService = userService;
  }

  /**
   * Converts a UserRegistrationDto to a User entity.
   *
   * @param dto The UserRegistrationDto to convert.
   * @return The User entity.
   */
  public User toEntity(UserRegistrationDto dto) {
    if (dto == null) {
      return null;
    }
    return new User(
        dto.username(),
        dto.email(),
        userService.hashPassword(dto.password()),
        dto.name(),
        dto.surname()
    );
  }

  /**
   * Converts a User entity to a UserRegistrationDto.
   *
   * @param entity The User entity to convert.
   * @return The UserRegistrationDto.
   */
  public UserRegistrationDto toDto(User entity) {
    if (entity == null) {
      return null;
    }
    return new UserRegistrationDto(
        entity.getUsername(),
        entity.getEmail(),
        "********", // Placeholder for the password
        entity.getName(),
        entity.getSurname()
    );
  }
}