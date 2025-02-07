package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import it.unisannio.studenti.qualitag.security.service.AuthenticationService;
import it.unisannio.studenti.qualitag.security.service.JwtService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserServiceTest {

  @Mock
  private ProjectService projectService;
  @Mock
  private TagService tagService;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;
  @Mock
  private TeamRepository teamRepository;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private ArtifactRepository artifactRepository;
  @Mock
  private TagRepository tagRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserService userService;

  private UserModifyDto userModifyDto;
  private PasswordUpdateDto passwordUpdateDto;
  private User user1, user2, owner;
  private Tag tag1, tag2;
  private Project project;
  private Team team;
  private Artifact artifact;


  /**
   * Sets up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    //Initialize the users object
    user1 = new User("user1", "user1@example.com",
        "password1", "Jane", "Doe");
    user1.setUserId("user1Id");
    user2 = new User("user2", "user2@example.com",
        "password2", "John", "Doe");
    user2.setUserId("user2Id");
    owner = new User("owner", "owner@example.com",
        "password2", "Alice", "Smith");
    owner.setUserId("ownerId");

    //Initialize the project
    long creationDate = Instant.now().toEpochMilli();
    long deadline = Instant.parse("2025-12-31T23:59:59Z").toEpochMilli();
    project = new Project("projectName", "projectDescription",
        creationDate, deadline, owner.getUserId(), new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("ownerId", "user1Id", "user2Id")));
    user1.setProjectIds(new ArrayList<>(List.of("projectId")));
    owner.setProjectIds(new ArrayList<>(List.of("projectId")));

    //Initialize the tag objects
    tag1 = new Tag("tag1", "user1Id", "fff8de");
    tag1.setTagId("6744ba6c60e0564864250e89");
    tag2 = new Tag("tag2", "user1Id", "#295f98");
    tag2.setTagId("6755b79afc22f97c06a34275");
    user1.setTagIds(new ArrayList<>(
        Arrays.asList("6744ba6c60e0564864250e89", "6755b79afc22f97c06a34275")));

    //Initialize the team object
    team = new Team("teamName", "projectId",
        123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("user1Id", "user2Id")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(List.of("teamId")));
    user2.setTeamIds(new ArrayList<>(List.of("teamId")));

    //Initialize the artifact object
    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    artifact.setArtifactId("artifactId");
    artifact.setTags(new ArrayList<>(Arrays.asList("tagId1", "tagId2")));

    //Initialize the dto
    userModifyDto = new UserModifyDto("user1Figo", "user1Figo@example.com",
        "Jane", "Doe");
    passwordUpdateDto = new PasswordUpdateDto("pAssword12$",
        "pAssword12$");

    //Initialize authorization details
    // Mock SecurityContextHolder to provide an authenticated user
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("user1");
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user1));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }


  /**
   * Tests a successful execution of getUSer method
   */
  @Test
  void testGetUser_Success() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.getUser("user1");

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /**
   * Tests an execution of the getUser method when the user is not found
   */
  @Test
  void testGetUser_NotFound() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(null);

    //Act
    ResponseEntity<?> response = userService.getUser("user1");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  /**
   * Tests a successful execution of getUserTags method
   */
  @Test
  void testGetUserTags_Success() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(tagRepository.findTagByTagId("6744ba6c60e0564864250e89")).thenReturn(tag1);
    when(tagRepository.findTagByTagId("6755b79afc22f97c06a34275")).thenReturn(tag2);

    //Act
    ResponseEntity<?> response = userService.getUserTags("user1");

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags retrieved successfully.");
    responseBody.put("tags", Arrays.asList(tag1, tag2));
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of getUserTags method when the user is not authorized
   */
  @Test
  void testGetUserTags_Unauthorized() {
    //Arrange
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("otherUser");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //Act
    ResponseEntity<?> response = userService.getUserTags("user1");

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "You are not authorized to access to this user.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of getUserTags method when the user is not found
   */
  @Test
  void testGetUserTags_UserNotFound() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(null);

    //Act
    ResponseEntity<?> response = userService.getUserTags("user1");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of updateUser method
   */
  @Test
  void testUpdateUser_Success() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.existsByUsername("user1Figo")).thenReturn(false);
    when(userRepository.existsByEmail("user1Figo@example.com")).thenReturn(false);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updateUser(userModifyDto, "user1");

    //Assert
    verify(jwtService, times(1)).generateToken
        (new CustomUserDetails(user1));

    verify(userMapper, times(1)).updateEntity(userModifyDto, user1);
    verify(userRepository, times(1)).save(user1);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User updated successfully.");
    responseBody.put("wholeuser", null);
    responseBody.put("token", null);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of updateUser method when the user doesn't change is username or
   * email
   */
  @Test
  void testUpdateUser_NoChange() {
    //Arrange
    UserModifyDto noChangeUserModifyDto = new UserModifyDto("user1",
        "user1@example.com", "Jane", "Doe");
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.existsByUsername("user1Figo")).thenReturn(false);
    when(userRepository.existsByEmail("user1Figo@example.com")).thenReturn(false);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updateUser(noChangeUserModifyDto, "user1");

    //Assert
    verify(jwtService, times(1)).generateToken
        (new CustomUserDetails(user1));

    verify(userMapper, times(1))
        .updateEntity(noChangeUserModifyDto, user1);
    verify(userRepository, times(1)).save(user1);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User updated successfully.");
    responseBody.put("wholeuser", null);
    responseBody.put("token", null);
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests and execution of updateUser method when the user is not authorized
   */
  @Test
  void testUpdateUser_Unauthorized() {
    //Arrange
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("otherUser");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //Act
    ResponseEntity<?> response = userService.updateUser(userModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "You are not authorized to modify this user.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateUser method when not all fields are filled
   */
  @Test
  void testUpdateUser_MissingFields() {
    //Arrange
    UserModifyDto invalidUserModifyDto = new UserModifyDto("user1Figo",
        "user1Figo@exaple.com", "", "");
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.existsByUsername("user1Figo")).thenReturn(false);
    when(userRepository.existsByEmail("user1Figo@example.com")).thenReturn(false);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updateUser(invalidUserModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateUser method when the new username is invalid
   */
  @Test
  void testUpdateUser_InvalidUsername() {
    //Arrange
    UserModifyDto invalidUserModifyDto = new UserModifyDto("ab",
        "user1Figo@example.com", "Jane", "Doe");
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.existsByUsername("ab")).thenReturn(false);
    when(userRepository.existsByEmail("user1Figo@example.com")).thenReturn(false);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updateUser(invalidUserModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid username.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateUser method when the new email is invalid
   */
  @Test
  void testUpdateUser_InvalidEmail() {
    //Arrange
    UserModifyDto invalidUserModifyDto = new UserModifyDto("user1Figo",
        "user1Figo", "Jane", "Doe");
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.existsByUsername("user1Figo")).thenReturn(false);
    when(userRepository.existsByEmail("user1Figo")).thenReturn(false);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updateUser(invalidUserModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid email address.");
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests an execution of updateUser method when the user is not found
   */
  @Test
  void testUpdateUser_UserNotFound() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(null);

    //Act
    ResponseEntity<?> response = userService.updateUser(userModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateUser method when the new username is already taken
   */
  @Test
  void testUpdateUser_UsernameTaken() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.existsByUsername("user1Figo")).thenReturn(true);

    //Act
    ResponseEntity<?> response = userService.updateUser(userModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Username already taken.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateUser method when the new email is already taken
   */
  @Test
  void testUpdateUser_EmailTaken() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.existsByUsername("user1Figo")).thenReturn(false);
    when(userRepository.existsByEmail("user1Figo@example.com")).thenReturn(true);

    //Act
    ResponseEntity<?> response = userService.updateUser(userModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Email already taken.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the updatePassword method
   */
  @Test
  void testUpdatePassword() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updatePassword(passwordUpdateDto,
        "user1");

    System.out.println(response.getBody());

    //Assert
    verify(userMapper, times(1)).updateEntity(passwordUpdateDto, user1);
    verify(userRepository, times(1)).save(user1);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Password updated successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updatePassword method when the user is not authorized
   */
  @Test
  void testUpdatePassword_Unauthorized() {
    //Arrange
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("otherUser");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updatePassword(passwordUpdateDto,
        "user1");

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "You are not authorized to modify this user.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updatePassword method when not all fields are filled
   */
  @Test
  void testUpdatePassword_MissingFields() {
    //Arrange
    PasswordUpdateDto invalidPasswordUpdateDto = new PasswordUpdateDto("pAssword12$",
        "");
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updatePassword(invalidPasswordUpdateDto,
        "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updatePassword method when the password is invalid
   */
  @Test
  void testUpdatePassword_InvalidPassword() {
    //Arrange
    PasswordUpdateDto invalidPasswordUpdateDto = new PasswordUpdateDto("password",
        "password");

    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updatePassword(invalidPasswordUpdateDto,
        "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid password.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updatePassword method when new password doesn't match
   */
  @Test
  void testUpdatePassword_PasswordsDontMatch() {
    //Arrange
    PasswordUpdateDto invalidPasswordUpdateDto = new PasswordUpdateDto(
        "pAssword12$", "pAssword12$1");

    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updatePassword(invalidPasswordUpdateDto,
        "user1");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Passwords do not match.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updatePassword method when the user is not found
   */
  @Test
  void testUpdatePassword_UserNotFound() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(null);

    //Act
    ResponseEntity<?> response = userService.updatePassword(passwordUpdateDto,
        "user1");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /***
   * Tests a successful execution of the deleteUser method
   */
  @Test
  void testDeleteUser_Success() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.deleteUser("user1");

    //Assert
    verify(userRepository, times(1)).deleteByUsername("user1");
    verify(projectRepository, times(1)).save(project);
    verify(teamRepository, times(1)).save(team);
    verify(tagService, times(1)).deleteTag(tag1.getTagId());
    verify(tagService, times(1)).deleteTag(tag2.getTagId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User deleted successfully.");
    assertEquals(responseBody, response.getBody());
    assertEquals(Arrays.asList("ownerId", "user2Id"), project.getUserIds());
    assertEquals(List.of("user2Id"), team.getUserIds());
  }

  /**
   * Tests an execution of the deleteUser method when the user is not authorized
   */
  @Test
  void testDeleteUser_Unauthorized() {
    //Arrange
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("otherUser");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //Act
    ResponseEntity<?> response = userService.deleteUser("user1");

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "You are not authorized to delete this user.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteUser method when the user is not found
   */
  @Test
  void testDeleteUser_UserNotFound() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(null);

    //Act
    ResponseEntity<?> response = userService.deleteUser("user1");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the deleteUser method when the user is the owner of the
   * project
   */
  @Test
  void testDeleteUser_UserIsOwner() {
    //Authentication
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("owner");
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user1));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //Arrange
    when(userRepository.findByUsername("owner")).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(userRepository.save(owner)).thenReturn(owner);

    //Act
    ResponseEntity<?> response = userService.deleteUser("owner");

    //Assert
    verify(userRepository, times(1)).deleteByUsername("owner");
    verify(projectService, times(1)).deleteProject(project.getProjectId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User deleted successfully.");
    assertEquals(responseBody, response.getBody());
  }

}
