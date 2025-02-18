package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.constants.TeamConstants;
import it.unisannio.studenti.qualitag.dto.team.CompletedTeamCreateDto;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.exception.TeamValidationException;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test class for the TeamService.
 */
public class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ArtifactRepository artifactRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private ArtifactService artifactService;

  @Mock
  private PythonClientService pythonClientService;

  @InjectMocks
  private TeamService teamService;

  private TeamCreateDto teamCreateDto;
  // private CompletedTeamCreateDto completedTeamCreateDto;
  private Team team;

  private Project project;

  private User owner;
  private User user1;
  private User user2;
  private User user3;

  private Artifact artifact;

  private Tag tag1;
  private Tag tag2;

  /**
   * Sets up the test environment.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Initialize entities
    team = new Team("teamName", "projectId", Instant.now().toEpochMilli(), "teamDescription",
        new ArrayList<>(Arrays.asList("user1Id", "user2Id")));
    team.setTeamId("teamId");

    long creationDate = Instant.now().toEpochMilli();
    long deadline = Instant.parse("2025-12-31T23:59:59Z").toEpochMilli();
    project = new Project("projectName", "projectDescription", creationDate, deadline, "ownerId",
        new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(Arrays.asList("ownerId", "user1Id", "user2Id", "user3Id"));
    owner = new User("username", "user@example.com", "hashedPassword123", "John", "Doe");
    owner.setUserId("ownerId");

    // additional users
    user1 = new User("user1", "user1@example.com", "password", "Jane", "Doe");
    user1.setUserId("user1Id");
    user1.setTeamIds(new ArrayList<>(List.of("teamId")));
    user2 = new User("user2", "user2@example.com", "password", "Alice", "Smith");
    user2.setUserId("user2Id");
    user2.setTeamIds(new ArrayList<>(List.of("teamId")));
    user3 = new User("user3", "user3@example.com", "password", "Bob", "Johnson");
    user3.setUserId("user3Id");
    user3.setTeamIds(new ArrayList<>());

    // tags and artifacts
    tag1 = new Tag("6744ba6c60e0564864250e89", "tag1Name", "projectId");
    tag1.setCreatedBy(user1.getUserId());
    tag2 = new Tag("6755b79afc22f97c06a34275", "tag2Name", "projectId");
    tag2.setCreatedBy(user2.getUserId());

    artifact = new Artifact("artifactName", "projectId", "teamId", "filePath");
    artifact.setArtifactId("artifactId");
    artifact.setTags(new ArrayList<>(Arrays.asList("tagId1", "tagId2")));

    // add artifact to team
    team.setArtifactIds(List.of("artifactId"));

    // Initialize DTO
    teamCreateDto = new TeamCreateDto("teamName", "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));
    // completedTeamCreateDto = new CompletedTeamCreateDto("teamName",
    // "projectId", 123456789L, "teamDescription",
    // Arrays.asList("user1", "user2")
    // );

    // Mock SecurityContextHolder to provide an authenticated user
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(owner));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

  }

  /**
   * Tests a successful execution of the addTeam Method.
   */
  @Test
  void testAddTeamsuccess() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.save(any(Team.class))).thenReturn(team);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team added successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the TeamCreateDto is invalid.
   */
  @Test
  void testAddTeamInvalidDto() {
    // Arrange
    TeamCreateDto invalidDto = new TeamCreateDto("", "", "", new ArrayList<>());

    // Act
    ResponseEntity<?> response = teamService.addTeam(invalidDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid team data.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the project is not found.
   *
   * @throws TeamValidationException if the project is not found
   */
  @Test
  void testAddTeamProjectNotFound() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the user is not the project owner.
   *
   * @throws TeamValidationException if the user is not the project owner
   */
  @Test
  void testAddTeamUserNotProjectOwner() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    project.setOwnerId("NotOwnerId");

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Only the project owner can create a team.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the team name is too short.
   *
   * @throws TeamValidationException if the team name is too short
   */
  @Test
  void testAddTeamNameTooShort() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("aa", "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("user2@example.com", "user3@example.com")));

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team name is too short.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * tests an execution of the addTeam method where the team name is too long.
   *
   * @throws TeamValidationException if the team name is too long
   */
  @Test
  void testAddTeamNameTooLong() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("teamNameIsTooLong", "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("user2@example.com", "user3@example.com")));

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team name is too long.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the owner email is in the list.
   *
   * @throws TeamValidationException if the owner email is in the list
   */
  @Test
  void testAddTeamOwnerEmailNotInList() throws TeamValidationException {
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription", "projectId", new ArrayList<>(
        Arrays.asList("user@example.com", "user1@example.com", "user2@example.com")));

    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner email must not be in the list.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the team has not enough users.
   *
   * @throws TeamValidationException if the team has not enough users
   */
  @Test
  void testAddTeamNotEnoughUsers() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription", "projectId",
        new ArrayList<>(List.of(user2.getEmail())));

    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg",
        "A team must have at least " + TeamConstants.MIN_TEAM_USERS + " users.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the team has too many users.
   *
   * @throws TeamValidationException if the team has too many users
   */
  @Test
  void testAddTeamTooManyUsers() throws TeamValidationException {
    // Creation of all the users
    User user3 = new User("user3", "user3@example.com", "password", "FirstName3", "LastName3");
    user3.setUserId("user3Id");

    User user4 = new User("user4", "user4@example.com", "password", "FirstName4", "LastName4");
    user4.setUserId("user4Id");

    User user5 = new User("user5", "user5@example.com", "password", "FirstName5", "LastName5");
    user5.setUserId("user5Id");

    User user6 = new User("user6", "user6@example.com", "password", "FirstName6", "LastName6");
    user6.setUserId("user6Id");

    User user7 = new User("user7", "user7@example.com", "password", "FirstName7", "LastName7");
    user7.setUserId("user7Id");

    User user8 = new User("user8", "user8@example.com", "password", "FirstName8", "LastName8");
    user8.setUserId("user8Id");

    User user9 = new User("user9", "user9@example.com", "password", "FirstName9", "LastName9");
    user9.setUserId("user9Id");

    User user10 = new User("user10", "user10@example.com", "password", "FirstName10", "LastName10");
    user10.setUserId("user10Id");

    User user11 = new User("user11", "user11@example.com", "password", "FirstName11", "LastName11");
    user11.setUserId("user11Id");

    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com", "user3@example.com",
            "user4@example.com", "user5@example.com", "user6@example.com", "user7@example.com",
            "user8@example.com", "user9@example.com", "user10@example.com", "user11@example.com")));

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByEmail(user3.getEmail())).thenReturn(user3);
    when(userRepository.findByEmail(user4.getEmail())).thenReturn(user4);
    when(userRepository.findByEmail(user5.getEmail())).thenReturn(user5);
    when(userRepository.findByEmail(user6.getEmail())).thenReturn(user6);
    when(userRepository.findByEmail(user7.getEmail())).thenReturn(user7);
    when(userRepository.findByEmail(user8.getEmail())).thenReturn(user8);
    when(userRepository.findByEmail(user9.getEmail())).thenReturn(user9);
    when(userRepository.findByEmail(user10.getEmail())).thenReturn(user10);
    when(userRepository.findByEmail(user11.getEmail())).thenReturn(user11);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(user3.getUserId())).thenReturn(user3);
    when(userRepository.findByUserId(user4.getUserId())).thenReturn(user4);
    when(userRepository.findByUserId(user5.getUserId())).thenReturn(user5);
    when(userRepository.findByUserId(user6.getUserId())).thenReturn(user6);
    when(userRepository.findByUserId(user7.getUserId())).thenReturn(user7);
    when(userRepository.findByUserId(user8.getUserId())).thenReturn(user8);
    when(userRepository.findByUserId(user9.getUserId())).thenReturn(user9);
    when(userRepository.findByUserId(user10.getUserId())).thenReturn(user10);
    when(userRepository.findByUserId(user11.getUserId())).thenReturn(user11);

    project.setUserIds(Arrays.asList("ownerId", "user1Id", "user2Id", "user3Id", "user4Id",
        "user5Id", "user6Id", "user7Id", "user8Id", "user9Id", "user10Id", "user11Id"));

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg",
        "A team cannot have more than " + TeamConstants.MAX_TEAM_USERS + " users.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where there is an empty email in the list.
   *
   * @throws TeamValidationException if there is an empty email in the list
   */
  @Test
  void testAddTeamEmptyEmail() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("", "user2@example.come")));

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "There is an empty email in the list. Remove it.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where there is a null email in the list.
   */
  @Test
  void testAddTeamNullEmail() {
    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList(null, "user2@example.come")));

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "There is an empty email in the list. Remove it.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the email does not exist.
   *
   * @throws TeamValidationException if the email does not exist
   */
  @Test
  void addTeamEmailDoesNotExist() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User with email " + user1.getEmail() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the team description is too long.
   *
   * @throws TeamValidationException if the team description is too long
   */
  @Test
  void addTeamTeamDescriptionTooLong() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("teamName",
        "This is a very long team description that is intentionally written to"
            + " exceed the maximum allowed length of 300 characters. The purpose of this "
            + "description is to test the validation logic in the application to ensure that it "
            + "correctly identifies and handles descriptions that are too long. "
            + "By including a variety of words and phrases, we can make sure that the description "
            + "is sufficiently lengthy to trigger the validation error. This should be more than "
            + "enough characters to exceed the limit.",
        "projectId", new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user2);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team description is too long. Max is "
        + TeamConstants.MAX_TEAM_DESCRIPTION_LENGTH + " characters including whitespaces.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTeam method where the team description is empty.
   *
   * @throws NoSuchMethodException if the validateTeam method is not found
   * @throws InvocationTargetException if the validateTeam method cannot be invoked
   * @throws IllegalAccessException if the validateTeam method cannot be accessed
   */
  @Test
  void testAddTeamTeamDescriptionEmpty()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Arrange
    teamCreateDto = new TeamCreateDto("teamName", null, "projectId",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    // Arrange: Set up mock objects and spy on the service class
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);

    TeamService teamServiceSpy = Mockito.spy(teamService);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Use reflection to invoke the private method 'validateTeam'
    Method validateTeamMethod =
        TeamService.class.getDeclaredMethod("validateTeam", TeamCreateDto.class);
    validateTeamMethod.setAccessible(true); // Make the method accessible

    CompletedTeamCreateDto result =
        (CompletedTeamCreateDto) validateTeamMethod.invoke(teamServiceSpy, teamCreateDto);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Hi, we are team teamName!", result.teamDescription());
  }

  /**
   * Tests an execution of the addTeam method where the user does not exist in the database.
   *
   * @throws TeamValidationException if the user does not exist
   */
  @Test
  void testAddTeamUserIdDoesNotExist() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.save(any(Team.class))).thenReturn(team);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(null);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByEmail(user2.getUserId())).thenReturn(user2);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + user1.getUserId() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  // TODO: Add test for when an user is the team is already in an another team

  /**
   * Tests the addTeam method where the user is not part of the project.
   */
  @Test
  void testAddTeamUserIsNotPartOfProject() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.save(any(Team.class))).thenReturn(team);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByEmail(user2.getUserId())).thenReturn(user2);

    project.setUserIds(Arrays.asList("ownerId", "user2Id"));

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + user1.getUserId() + " is not part of the project.");
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests and AddTeam execution by using reflection to see if the CompletedTeamCreateDto is created
   * correctly.
   *
   * @throws NoSuchMethodException if the validateTeam method is not found
   * @throws InvocationTargetException if the validateTeam method cannot be invoked
   * @throws IllegalAccessException if the validateTeam method cannot be accessed
   */
  @Test
  void testAddTeamsuccessusingReflection()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    // Mock necessary repository calls
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);

    // Arrange: Set up mock objects and spy on TeamService
    TeamService teamServiceSpy = Mockito.spy(teamService);

    // Prepare DTO
    TeamCreateDto teamCreateDto = new TeamCreateDto("teamName", "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    // Prepare the expected CompletedTeamCreateDto
    CompletedTeamCreateDto expectedDto = new CompletedTeamCreateDto("teamName", "projectId",
        123456789L, "teamDescription", Arrays.asList("user1Id", "user2Id"));

    // Use reflection to invoke the private method 'validateTeam'
    Method validateTeamMethod =
        TeamService.class.getDeclaredMethod("validateTeam", TeamCreateDto.class);
    validateTeamMethod.setAccessible(true); // Make the method accessible

    // Act: Invoke the private method
    CompletedTeamCreateDto result =
        (CompletedTeamCreateDto) validateTeamMethod.invoke(teamServiceSpy, teamCreateDto);

    // Assert: Check if 'creationDate' is non-negative and if userIds match
    assertTrue(result.creationDate() >= 0); // Ensure creationDate is valid (non-negative)
    assertEquals(expectedDto.teamName(), result.teamName());
    assertEquals(expectedDto.projectId(), result.projectId());
    assertEquals(expectedDto.teamDescription(), result.teamDescription());
    assertEquals(expectedDto.userIds(), result.userIds());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the team is null.
   */
  @Test
  void testAddTeamToUsersTeamNull() {
    // Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);
    team = null;

    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod =
          TeamService.class.getDeclaredMethod("addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause, "Expected a TeamValidationException.");
    assertEquals("Team cannot be null.", cause.getMessage());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the team has a null user.
   */
  @Test
  void testAddTeamToUsersUserIdNull() {
    // Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);

    team.setUserIds(Arrays.asList(null, "user1Id"));

    // Act & Assert
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod =
          TeamService.class.getDeclaredMethod("addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert the cause
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause, "Expected a TeamValidationException.");
    assertEquals("User ID is null or empty.", cause.getMessage());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the team has an empty user.
   */
  @Test
  void testAddTeamToUsersUserIdEmpty() {
    // Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);

    team.setUserIds(Arrays.asList("", "user1Id"));

    // Act & Assert
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod =
          TeamService.class.getDeclaredMethod("addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert the cause
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause, "Expected a TeamValidationException.");
    assertEquals("User ID is null or empty.", cause.getMessage());
  }


  /**
   * Tests the method addTeamToUsers with reflection, in the case where the user does not exist.
   */
  @Test
  void testAddTeamToUsersUserDoesNotExist() {
    // Arrange

    team.setUserIds(Arrays.asList("nonexistentUser", user1.getUserId()));
    when(userRepository.findByUserId("nonexistentUser")).thenReturn(null);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);

    TeamService teamServiceSpy = Mockito.spy(teamService);

    // Act & Assert
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod =
          TeamService.class.getDeclaredMethod("addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert the cause
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause, "Expected a TeamValidationException.");
    assertEquals("User with ID nonexistentUser does not exist.", cause.getMessage());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the user is not part of the
   * project.
   */
  @Test
  void testAddTeamToUsersUserNotInProject() {
    // Arrange
    team.setProjectId(project.getProjectId());
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);

    TeamService teamServiceSpy = Mockito.spy(teamService);

    project.setUserIds(Arrays.asList("user1Id", "user3Id")); // User not in project
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);

    // Act
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod =
          TeamService.class.getDeclaredMethod("addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause, "Expected a TeamValidationException.");
    assertEquals("User with ID " + user2.getUserId() + " is not part of the project.",
        cause.getMessage());
  }

  /**
   * Tests a successful execution of the getTeamsByProject method.
   */
  @Test
  void testGetTeamsByProject() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(teamRepository.existsByProjectId(project.getProjectId())).thenReturn(true);
    when(teamRepository.findTeamsByProjectId(project.getProjectId())).thenReturn(List.of(team));

    // Act
    ResponseEntity<?> response = teamService.getTeamsByProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(List.of(team), response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByProject method where the project is null.
   */
  @Test
  void testGetTeamsByProjectProjectNull() {
    // Act
    ResponseEntity<?> response = teamService.getTeamsByProject(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Project ID is null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByProject method where the project is empty.
   */
  @Test
  void testGetTeamsByProjectProjectEmpty() {
    // Act
    ResponseEntity<?> response = teamService.getTeamsByProject("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Project ID is null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByProject method where there no teams found for the project.
   */
  @Test
  void testGetTeamsByProjectNoTeamsFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(teamRepository.existsByProjectId(project.getProjectId())).thenReturn(false);

    // Act
    ResponseEntity<?> response = teamService.getTeamsByProject("projectId");

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No teams found for project ID projectId.", response.getBody());
  }

  /**
   * Tests a successful execution of the getTeamByUser method.
   */
  @Test
  void testGetTeamsByUser() {
    // Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(teamRepository.existsByUserIdsContaining(user1.getUserId())).thenReturn(true);
    when(teamRepository.findByUserIdsContaining(user1.getUserId())).thenReturn(List.of(team));

    // Act
    ResponseEntity<?> response = teamService.getTeamsByUser(user1.getUserId());

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(List.of(team), response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByUser method where the user is null.
   */
  @Test
  void testGetTeamsByUserUserNull() {
    // Act
    ResponseEntity<?> response = teamService.getTeamsByUser(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User ID is null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByUser method where the user is empty.
   */
  @Test
  void testGetTeamsByUserUserEmpty() {
    // Act
    ResponseEntity<?> response = teamService.getTeamsByUser("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User ID is null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByUser method where there are no teams found for the user.
   */
  @Test
  void testGetTeamsByUserNoTeamsFound() {
    // Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(teamRepository.existsByUserIdsContaining(user1.getUserId())).thenReturn(false);

    // Act
    ResponseEntity<?> response = teamService.getTeamsByUser(user1.getUserId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No teams found for user ID " + user1.getUserId() + ".", response.getBody());
  }

  /**
   * Tests a successful execution of the deleteTeam method.
   */
  @Test
  void testDeleteTeam() {
    // Arrange
    when(teamRepository.findById(team.getTeamId())).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(project);

    when(userRepository.findById(user2.getUserId())).thenReturn(Optional.ofNullable(user2));
    when(userRepository.save(user2)).thenReturn(user2);
    when(userRepository.findById(user1.getUserId())).thenReturn(Optional.ofNullable(user1));
    when(userRepository.save(user1)).thenReturn(user1);
    when(teamRepository.existsById(team.getTeamId())).thenReturn(false);

    when(artifactService.deleteArtifact(artifact.getArtifactId()))
        .thenReturn(ResponseEntity.ok().build());

    // Act
    ResponseEntity<?> response = teamService.deleteTeam(team.getTeamId());

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team deleted successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTeam method where the team is null.
   */
  @Test
  void testDeleteTeamTeamNull() {
    // Act
    ResponseEntity<?> response = teamService.deleteTeam(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team ID is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTeam method where the team is empty.
   */
  @Test
  void testDeleteTeamTeamEmpty() {
    // Act
    ResponseEntity<?> response = teamService.deleteTeam("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team ID is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTeam method where the team is not found.
   */
  @Test
  void testDeleteTeamTeamNotFound() {
    // Arrange
    when(teamRepository.findById(team.getTeamId())).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = teamService.deleteTeam(team.getTeamId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the deleteTeam method where the project is not found.
   */
  @Test
  void testDeleteTeamProjectNotFound() {
    // Arrange
    when(teamRepository.findById(team.getTeamId())).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.deleteTeam("teamId");

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTeam method where the user is not found.
   */
  @Test
  void testDeleteTeamUserNotFound() {
    // Arrange
    when(teamRepository.findById(team.getTeamId())).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(project);

    when(userRepository.findById(user1.getUserId())).thenReturn(Optional.ofNullable(user1));
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.findById(user2.getUserId())).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = teamService.deleteTeam(team.getTeamId());

    // Assert
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + user2.getUserId() + " not found.");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTeam method where the team is not deleted.
   */
  @Test
  void testDeleteTeamTeamNotDeleted() {
    // Arrange
    when(teamRepository.findById(team.getTeamId())).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(project);

    when(userRepository.findById(user2.getUserId())).thenReturn(Optional.ofNullable(user2));
    when(userRepository.save(user2)).thenReturn(user2);
    when(userRepository.findById(user1.getUserId())).thenReturn(Optional.ofNullable(user1));
    when(userRepository.save(user1)).thenReturn(user1);
    when(teamRepository.existsById(team.getTeamId())).thenReturn(true);

    when(artifactService.deleteArtifact(artifact.getArtifactId()))
        .thenReturn(ResponseEntity.ok().build());

    // Act
    ResponseEntity<?> response = teamService.deleteTeam(team.getTeamId());

    // Assert
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team not deleted.");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getTeamIrr method.
   */
  @Test
  void testGetTeamIrrSuccess() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    String mockAlphaResponse = "{\"alpha\": 0.85}";
    when(pythonClientService.getKrippendorffAlpha(Mockito.anyList())).thenReturn(mockAlphaResponse);

    // Act
    ResponseEntity<?> response = teamService.getTeamIrr(team.getTeamId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Successfully retrieved Krippendorff's alpha.");
    responseBody.put("irr", 0.85);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTeamIrr method where the team is not found.
   */
  @Test
  void testGetTeamIrrTeamNotFound() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.getTeamIrr(team.getTeamId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTeamIrr method where the artifact is not found.
   */
  @Test
  void testGetTeamIrrArtifactNotFound() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.getTeamIrr(team.getTeamId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertNotNull(responseBody);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTeamIrr method where the tag is not found.
   */
  @Test
  void testGetTeamIrrNoTagsForArtifact() {
    // Arrange
    artifact.setTags(new ArrayList<>()); // Artifact has no tags
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);

    String mockAlphaResponse = "{\"alpha\": 1.0}";
    when(pythonClientService.getKrippendorffAlpha(Mockito.anyList())).thenReturn(mockAlphaResponse);

    // Act
    ResponseEntity<?> response = teamService.getTeamIrr(team.getTeamId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Successfully retrieved Krippendorff's alpha.");
    responseBody.put("irr", 1.0); // Default alpha value
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTeamIrr method where the tag creator is mismatched.
   */
  @Test
  void testGetTeamIrrTagCreatedByUserMismatch() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);

    tag1.setCreatedBy("otherUserId"); // No user in the team matches this creator
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    String mockAlphaResponse = "{\"alpha\": 0.0}";
    when(pythonClientService.getKrippendorffAlpha(Mockito.anyList())).thenReturn(mockAlphaResponse);

    // Act
    ResponseEntity<?> response = teamService.getTeamIrr(team.getTeamId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Successfully retrieved Krippendorff's alpha.");
    responseBody.put("irr", 0.0); // Alpha drops due to mismatch
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test a successful execution of UpdateTeamUsers method.
   */
  @Test
  void testUpdateTeamUsersSuccess() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByEmail(user3.getEmail())).thenReturn(user3);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(user3.getUserId())).thenReturn(user3);
    when(userRepository.save(user3)).thenReturn(user3);

    List<String> userEmails = Arrays.asList(user1.getEmail(), user3.getEmail());

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(), userEmails);

    System.out.println(response.getBody());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team users updated successfully.");
    assertEquals(responseBody, response.getBody());
    verify(teamRepository).save(team);
    assertEquals(Arrays.asList(user1.getUserId(), user3.getUserId()), team.getUserIds());
    assertTrue(user3.getTeamIds().contains("teamId")); // This doesn't work apparently...
    assertFalse(user2.getTeamIds().contains("teamId"));
  }

  /**
   * Tests an execution of testUpdateUsers where the team is null.
   */
  @Test
  void testUpdateTeamUsersTeamNull() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(null);
    List<String> userEmails = Arrays.asList(user1.getEmail(), user3.getEmail());

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(), userEmails);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution for updateTeamUsers where the user is not found in the project.
   */
  @Test
  void testUpdateTeamUsersUserNotFoundProject() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(null);
    List<String> userEmails = Arrays.asList(user1.getEmail(), user3.getEmail());

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(), userEmails);

    //
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with email " + user1.getEmail() + " not found.");
    assertEquals(responseBody, response.getBody());

  }

  /**
   * Tests an execution of updateTeamUsers where the project is not found.
   */
  @Test
  public void testUpdateTeamUsersProjectNotFound() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(),
        Arrays.asList(user1.getEmail(), user3.getEmail()));

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateTeamUsers where the logged in user is not the project owner.
   */
  @Test
  public void testUpdateTeamUsersNotProjectOwner() {
    // Arrange
    project.setOwnerId("otherUserId");
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(),
        Arrays.asList(user1.getEmail(), user3.getEmail()));

    // Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Only the project owner can update the team users.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateTeamUsers where one of the users is not part of the project.
   */
  @Test
  public void testUpdateTeamUsersUserNotInProject() {
    // Arrange
    project.setUserIds(Arrays.asList("ownerId", "user2Id"));
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(),
        Arrays.asList(user1.getEmail(), user3.getEmail()));

    // Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with email " + user1.getEmail() + " is not part of the project.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateTeamUsers where the user is not found (in the database).
   */
  @Test
  public void testUpdateTeamUsersUserNotFound() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByEmail(user3.getEmail())).thenReturn(user3);

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(),
        Arrays.asList(user1.getEmail(), user3.getEmail()));

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + user1.getUserId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateTeamUsers where the user to add to team is not found.
   */
  @Test
  public void testUpdateTeamUsersUserToAddNotFound() {
    // Arrange
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByEmail(user3.getEmail())).thenReturn(user3);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(user3.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(),
        Arrays.asList(user1.getEmail(), user3.getEmail()));

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + user3.getUserId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of updateTeamUsers where the user to add is already in another team.
   */
  @Test
  public void testUpdateTeamUsersUserToAddAlreadyInTeam() {
    // Arrange
    Team anotherTeam = new Team("anotherTeam", project.getProjectId(), 123456789L,
        "teamDescription", new ArrayList<>(List.of(user3.getUserId())));
    anotherTeam.setTeamId("anotherTeamId");
    project.setTeamIds(Arrays.asList("teamId", "anotherTeamId"));
    user3.setTeamIds(new ArrayList<>(List.of("anotherTeamId")));

    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(teamRepository.findTeamByTeamId(anotherTeam.getTeamId())).thenReturn(anotherTeam);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByEmail(user3.getEmail())).thenReturn(user3);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(user3.getUserId())).thenReturn(user3);
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.save(user2)).thenReturn(user2);
    when(userRepository.save(user3)).thenReturn(user3);

    // Act
    ResponseEntity<?> response = teamService.updateTeamUsers(team.getTeamId(),
        Arrays.asList(user1.getEmail(), user3.getEmail()));

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team users updated successfully.");
    assertEquals(responseBody, response.getBody());
    assertEquals(Arrays.asList(user1.getUserId(), user3.getUserId()), team.getUserIds());
    assertTrue(user3.getTeamIds().contains("teamId"));
    assertFalse(user3.getTeamIds().contains("anotherTeamId"));
    assertFalse(anotherTeam.getUserIds().contains(user3.getUserId()));
    assertFalse(user2.getTeamIds().contains("teamId"));
  }



}

