package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserInfoDisplayDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.dto.user.UserResponseDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
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
   * Converts the user to a UserSortResponseDTO.
   *
   * @param user The user to convert.
   * @return The UserSortResponseDTO.
   */
  public static UserShortResponseDto toUserShortResponseDto(User user) {
    return new UserShortResponseDto(user.getUsername(),
        user.getEmail(),
        user.getName(),
        user.getSurname(),
        user.getProjectRolesAsString());
  }

  /**
   * CConverts the user to a UserResponseDto.
   *
   * @param user The user to convert.
   * @return The UserResponseDTO.
   */
  public static UserResponseDto toUserResponseDto(User user) {
    return new UserResponseDto(user.getUsername(),
        user.getEmail(),
        user.getName(),
        user.getSurname(),
        user.getProjectIds(),
        user.getTeamIds(),
        user.getTagIds(),
        user.getProjectRolesAsString(),
        user.getUserId()
    );
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
        passwordEncoder.encode(dto.password()),
        dto.name(),
        dto.surname()
    );
  }

  /**
   * Updates a User entity with the data from a UserModifyDto.
   *
   * @param dto    The UserRegistrationDto with the new data.
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
   * Updates a User entity with the data from a PasswordUpdateDto.
   *
   * @param dto    The PasswordUpdateDto with the new password.
   * @param entity The User entity to update.
   */
  public void updateEntity(PasswordUpdateDto dto, User entity) {
    if (dto == null || entity == null) {
      return;
    }
    entity.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
  }

  /**
   * Converts a User entity to a UserInfoDisplayDto.
   *
   * @param entity The User entity to convert.
   * @return The UserInfoDisplayDto.
   */
  public UserInfoDisplayDto toDisplayDto(User entity) {
    if (entity == null) {
      return null;
    }
    return new UserInfoDisplayDto(
        entity.getUsername(),
        entity.getEmail(),
        entity.getName(),
        entity.getSurname(),
        entity.getProjectIds(),
        entity.getTeamIds(),
        entity.getTagIds(),
        entity.getProjectRoles()
    );
  }
}