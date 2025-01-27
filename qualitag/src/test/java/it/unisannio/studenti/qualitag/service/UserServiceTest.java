package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.dto.user.UserInfoDisplayDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
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

  private UserRegistrationDto userRegistrationDto;
  private UserInfoDisplayDto userInfoDisplayDto;
  private UserModifyDto userModifyDto;
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

    //Initialize the tag objects
    tag1 = new Tag("tag1", "tag1Name", "projectId");
    tag1.setTagId("tag1Id");
    tag1.setCreatedBy(user1.getUserId());
    tag2 = new Tag("tag2", "tag2Name", "projectId");
    tag2.setTagId("tag2Id");
    tag2.setCreatedBy(user1.getUserId());
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
    userRegistrationDto = new UserRegistrationDto("user1", "user1@example.com",
        "password1", "Jane", "Doe");
    userInfoDisplayDto = new UserInfoDisplayDto("user1", "user1@example.com",
        "Jane", "Doe", user1.getProjectIds(), user1.getTeamIds(), user1.getTagIds(),
        user1.getProjectRoles());
    userModifyDto = new UserModifyDto("user1Figo", "user1@example.com",
        "Jane", "Doe");

    //Initiliaza authorization details
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
    when(userRepository.save(user1)).thenReturn(user1);

    //Act
    ResponseEntity<?> response = userService.updateUser(userModifyDto, "user1");

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User updated successfully.",
        ((Map<String, Object>) response.getBody()).get("msg"));
  }

}
