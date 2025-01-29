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
import java.util.ArrayList;
import java.util.Arrays;
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

    //Initiliaza the users object
    user1 = new User("user1", "user1@example.com",
        "password1", "Jane", "Doe");
    user1.setUserId("user1Id");
    user2 = new User("user2", "user2@example.com",
        "password2", "John", "Doe");
    user2.setUserId("user2Id");
    owner = new User("owner", "owner@example.com",
        "password2", "Alice", "Smith");
    owner.setUserId("ownerId");

    //Initializa the project
    project = new Project("projectName", "projectDescription",
        0L, 0L, "ownerId", new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("ownerId", "user1Id", "user2Id")));
    user1.setProjectIds(new ArrayList<>(Arrays.asList("projectId")));
    owner.setProjectIds(new ArrayList<>(Arrays.asList("projectId")));

    //Initialize the tag objects
    tag1 = new Tag("tag1", "user1Id", "fff8de");
    tag1.setTagId("tag1Id");
    tag2 = new Tag("tag2", "user1Id", "#295f98");
    tag2.setTagId("tag2Id");
    user1.setTagIds(new ArrayList<>(Arrays.asList("tag1Id", "tag2Id")));

    //Initialize the team object
    team = new Team("teamName", "projectId",
        123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("user1Id", "user2Id")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(Arrays.asList("teamId")));
    user2.setTeamIds(new ArrayList<>(Arrays.asList("teamId")));

    //Initialize the artifact object
    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    artifact.setArtifactId("artifactId");
    artifact.setTags(new ArrayList<>(Arrays.asList("tagId1", "tagId2")));

    //Initialize the dtos
    userModifyDto = new UserModifyDto("user1Figo", "user1Figo@example.com",
        "Jane", "Doe");
    passwordUpdateDto = new PasswordUpdateDto("pAssword12$",
        "pAssword12$");

    //Initiliaze authorization details
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
    when(tagRepository.findTagByTagId("tag1Id")).thenReturn(tag1);
    when(tagRepository.findTagByTagId("tag2Id")).thenReturn(tag2);

    //Act
    ResponseEntity<?> response = userService.getUserTags("user1");

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Tags retrieved successfully.",
        ((Map<String, Object>) response.getBody()).get("msg"));
    assertEquals(Arrays.asList(tag1, tag2),
        ((Map<String, Object>) response.getBody()).get("tags"));
  }

  /**
   * Tests an execution of getUserTags method when the user is not authorized
   */
  @Test
  void testGetUsetTags_Unauthorized() {
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
    assertEquals("You are not authorized to access to this user.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("User not found.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    ;
    verify(userMapper, times(1)).updateEntity(userModifyDto, user1);
    verify(userRepository, times(1)).save(user1);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User updated successfully.",
        ((Map<String, Object>) response.getBody()).get("msg"));

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
    ;
    verify(userMapper, times(1))
        .updateEntity(noChangeUserModifyDto, user1);
    verify(userRepository, times(1)).save(user1);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User updated successfully.",
        ((Map<String, Object>) response.getBody()).get("msg"));


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
    assertEquals("You are not authorized to modify this user.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("All fields must be filled.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("Invalid username.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("Invalid email address.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("User not found.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("Username already taken.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("Email already taken.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("Password updated successfully.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("You are not authorized to modify this user.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("All fields must be filled.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("Invalid password.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("Passwords do not match.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("User not found.",
        ((Map<String, Object>) response.getBody()).get("msg"));
  }

  /***
   * Tests a successful execution of the deleteUser method
   */
  @Test
  void testDeleteUser_Success() {
    //Arrange
    when(userRepository.findByUsername("user1")).thenReturn(user1);
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(teamRepository.findTeamByTeamId("teamId")).thenReturn(team);
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.deleteUser("user1");

    //Assert
    verify(userRepository, times(1)).deleteByUsername("user1");
    verify(projectRepository, times(1)).save(project);
    verify(teamRepository, times(1)).save(team);
    verify(tagService, times(1)).deleteTag("tag1Id");
    verify(tagService, times(1)).deleteTag("tag2Id");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User deleted successfully.",
        ((Map<String, Object>) response.getBody()).get("msg"));
    assertEquals(Arrays.asList("ownerId", "user2Id"), project.getUserIds());
    assertEquals(Arrays.asList("user2Id"), team.getUserIds());
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
    assertEquals("You are not authorized to delete this user.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    assertEquals("User not found.",
        ((Map<String, Object>) response.getBody()).get("msg"));
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
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(teamRepository.findTeamByTeamId("teamId")).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId("artifactId")).thenReturn(artifact);
    when(userRepository.save(owner)).thenReturn(owner);

    //Act
    ResponseEntity<?> response = userService.deleteUser("owner");

    //Assert
    verify(userRepository, times(1)).deleteByUsername("owner");
    verify(projectService, times(1)).deleteProject("projectId");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User deleted successfully.",
        ((Map<String, Object>) response.getBody()).get("msg"));
  }

}
