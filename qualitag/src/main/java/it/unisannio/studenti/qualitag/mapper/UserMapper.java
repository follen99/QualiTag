package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * The UserMapper class provides methods to convert between User entities and User DTOs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserMapper {

  private final PasswordEncoder passwordEncoder;

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
        passwordEncoder.encode(dto.password()),
        dto.name(),
        dto.surname()
    );
  }

  /**
   * Updates a User entity with the data from a UserModifyDto.
   *
   * @param dto The UserRegistrationDto with the new data.
   * @param entity The User entity to update.
   */
  public void updateEntity(UserModifyDto dto, User entity) {
    if (dto == null || entity == null) {
      return;
    }
    entity.setUsername(dto.username());
    entity.setEmail(dto.email());
    entity.setName(dto.name());
    entity.setSurname(dto.surname());
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