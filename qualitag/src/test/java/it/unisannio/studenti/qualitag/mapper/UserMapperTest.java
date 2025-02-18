package it.unisannio.studenti.qualitag.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserInfoDisplayDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.dto.user.UserResponseDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.security.config.PasswordConfig;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test class for the UserMapper class.
 */
public class UserMapperTest {

  private User entity;
  private UserShortResponseDto shortResponseDto;
  private UserResponseDto responseDto;
  private UserRegistrationDto registrationDto;
  private UserModifyDto modifyDto;
  private PasswordUpdateDto passwordUpdateDto;
  private UserInfoDisplayDto infoDisplayDto;
  private PasswordConfig passwordConfig;
  private PasswordEncoder passwordEncoder;
  private UserMapper userMapper;

  /**
   * Initializes the test environment.
   */
  @BeforeEach
  public void setUp() {
    passwordConfig = new PasswordConfig();
    passwordEncoder = passwordConfig.passwordEncoder();
    userMapper = new UserMapper(passwordEncoder);
    String passwordHash = passwordEncoder.encode("password");
    entity = new User("username", "user@example.com",
        passwordHash, "Mario", "Rossi");
    entity.setUserId("6744ba6c60e0564864250e89");
    shortResponseDto = new UserShortResponseDto("username",
        "user@example.com", "Mario", "Rossi", new HashMap<>());
    responseDto = new UserResponseDto("username", "user@example.com",
        "Mario", "Rossi", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new HashMap<>(), "6744ba6c60e0564864250e89");
    registrationDto = new UserRegistrationDto("username",
        "user@example.com", "password", "Mario", "Rossi");
    modifyDto = new UserModifyDto("newUsername", "userNew@example.com",
        "Mario", "Rossi");
    passwordUpdateDto = new PasswordUpdateDto("newPassword",
        "newPassword");
    infoDisplayDto = new UserInfoDisplayDto("username", "user@example.com",
        "Mario", "Rossi", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
        new HashMap<>());
  }

  /**
   * Tests the toShortResponseDto method of the UserMapper class.
   */
  @Test
  public void testToShortResponseDto() {
    UserShortResponseDto dtoFromEntity = UserMapper.toUserShortResponseDto(entity);
    assertEquals(shortResponseDto.username(), dtoFromEntity.username());
    assertEquals(shortResponseDto.email(), dtoFromEntity.email());
    assertEquals(shortResponseDto.name(), dtoFromEntity.name());
    assertEquals(shortResponseDto.surname(), dtoFromEntity.surname());
  }

  /**
   * Test the toUserResponseDto method of the UserMapper class.
   */
  @Test
  public void testToUserResponseDto() {
    UserResponseDto dtoFromEntity = UserMapper.toUserResponseDto(entity);
    assertEquals(responseDto.username(), dtoFromEntity.username());
    assertEquals(responseDto.email(), dtoFromEntity.email());
    assertEquals(responseDto.name(), dtoFromEntity.name());
    assertEquals(responseDto.surname(), dtoFromEntity.surname());
    assertEquals(responseDto.projectIds(), dtoFromEntity.projectIds());
    assertEquals(responseDto.teamIds(), dtoFromEntity.teamIds());
    assertEquals(responseDto.tagIds(), dtoFromEntity.tagIds());
    assertEquals(responseDto.projectRoles(), dtoFromEntity.projectRoles());
    assertEquals(responseDto.userId(), dtoFromEntity.userId());
  }

  /**
   * Tests the toEntity method of the UserMapper class.
   */
  @Test
  public void testToEntity() {
    User entityFromDto = userMapper.toEntity(registrationDto);
    assertEquals(entity.getUsername(), entityFromDto.getUsername());
    assertEquals(entity.getEmail(), entityFromDto.getEmail());
    assertEquals(entity.getName(), entityFromDto.getName());
    assertEquals(entity.getSurname(), entityFromDto.getSurname());

    assertTrue(passwordEncoder.matches(registrationDto.password(),
        entityFromDto.getPasswordHash()));
  }

  /**
   * Tests the toEntity method of the UserMapper class when the dto is null.
   */
  @Test
  public void testToEntityNull() {
    registrationDto = null;
    User entityFromDto = userMapper.toEntity(registrationDto);
    assertNull(entityFromDto);
  }

  /**
   * Tests the updateEntity (modifyDto) method of the UserMapper class.
   */
  @Test
  public void testUpdateModifyEntity() {
    userMapper.updateEntity(modifyDto, entity);
    assertEquals(modifyDto.username(), entity.getUsername());
    assertEquals(modifyDto.email(), entity.getEmail());
    assertEquals(modifyDto.name(), entity.getName());
    assertEquals(modifyDto.surname(), entity.getSurname());
  }

  /**
   * Tests the updateEntity (modifyDto) method of the UserMapper class when the dto is null.
   */
  @Test
  public void testUpdateModifyEntityNull() {
    modifyDto = null;
    userMapper.updateEntity(modifyDto, entity);
    assertEquals("username", entity.getUsername());
    assertEquals("user@example.com", entity.getEmail());
    assertEquals("Mario", entity.getName());
    assertEquals("Rossi", entity.getSurname());
  }

  /**
   * Tests the updateEntity (modifyDto) method of the UserMapper class when the entity is null.
   */
  @Test
  public void testUpdateModifyEntityNullEntity() {
    entity = null;
    userMapper.updateEntity(modifyDto, entity);
    assertNull(entity);
  }

  /**
   * Tests the updateEntity (modifyDto) method of the UserMapper class when the dto and the entity
   * are null.
   */
  @Test
  public void testUpdateModifyEntityNullBoth() {
    modifyDto = null;
    entity = null;
    userMapper.updateEntity(modifyDto, entity);
    assertNull(entity);
  }

  /**
   * Tests the updateEntity (passwordUpdateDto) method of the UserMapper class.
   */
  @Test
  public void testUpdatePasswordEntity() {
    userMapper.updateEntity(passwordUpdateDto, entity);
    assertTrue(passwordEncoder.matches(passwordUpdateDto.newPassword(),
        entity.getPasswordHash()));
  }

  /**
   * Tests the updateEntity (passwordUpdateDto) method of the UserMapper class when the dto is
   * null.
   */
  @Test
  public void testUpdatePasswordEntityNull() {
    passwordUpdateDto = null;
    userMapper.updateEntity(passwordUpdateDto, entity);
    assertTrue(passwordEncoder.matches("password",
        entity.getPasswordHash()));
  }

  /**
   * Tests the updateEntity (passwordUpdateDto) method of the UserMapper class when the entity is
   * null.
   */
  @Test
  public void testUpdatePasswordEntityNullEntity() {
    entity = null;
    userMapper.updateEntity(passwordUpdateDto, entity);
    assertNull(entity);
  }

  /**
   * Tests the updateEntity (passwordUpdateDto) method of the UserMapper class when the dto and the
   * entity are null.
   */
  @Test
  public void testUpdatePasswordEntityNullBoth() {
    passwordUpdateDto = null;
    entity = null;
    userMapper.updateEntity(passwordUpdateDto, entity);
    assertNull(entity);
  }

  /**
   * Tests the toDisplayDto method of the UserMapper class.
   */
  @Test
  public void testToDisplayDto() {
    UserInfoDisplayDto dtoFromEntity = userMapper.toDisplayDto(entity);
    assertEquals(infoDisplayDto.username(), dtoFromEntity.username());
    assertEquals(infoDisplayDto.email(), dtoFromEntity.email());
    assertEquals(infoDisplayDto.name(), dtoFromEntity.name());
    assertEquals(infoDisplayDto.surname(), dtoFromEntity.surname());
  }

  /**
   * Tests the toDisplayDto method of the UserMapper class when the entity is null.
   */
  @Test
  public void testToDisplayDtoNull() {
    entity = null;
    UserInfoDisplayDto dtoFromEntity = userMapper.toDisplayDto(entity);
    assertNull(dtoFromEntity);
  }


}
