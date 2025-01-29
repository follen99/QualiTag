package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
  private CompletedTeamCreateDto completedTeamCreateDto;
  private Team team;
  private Project project;
  private User owner, user1, user2, user3;
  private Artifact artifact;
  private Tag tag1, tag2;

  /**
   * Sets up the test environment.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    //Initialize entities
    team = new Team("teamName", "projectId",
        123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("user1Id", "user2Id")));
    team.setTeamId("teamId");

    project = new Project("projectName", "projectDescription",
        0L, 0L, "ownerId", new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(Arrays.asList("ownerId", "user1Id", "user2Id"));
    owner = new User("username", "user@example.com",
        "hashedPassword123", "John", "Doe");
    owner.setUserId("ownerId");

    //additional users
    user1 = new User("user1", "user1@example.com",
        "password", "Jane", "Doe");
    user1.setUserId("user1Id");
    user1.setTeamIds(new ArrayList<>(Arrays.asList("teamId")));
    user2 = new User("user2", "user2@example.com",
        "password", "Alice", "Smith");
    user2.setUserId("user2Id");
    user2.setTeamIds(new ArrayList<>(Arrays.asList("teamId")));
    user3 = new User("user3", "user3@example.com", "password",
        "Bob", "Johnson");
    user3.setUserId("user3Id");
    user3.setTeamIds(new ArrayList<>());

    //tags and artifacts
    tag1 = new Tag("tag1Id", "tag1Name", "projectId");
    tag1.setCreatedBy(user1.getUserId());
    tag2 = new Tag("tag2Id", "tag2Name", "projectId");
    tag2.setCreatedBy(user2.getUserId());

    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    artifact.setArtifactId("artifactId");
    artifact.setTags(new ArrayList<>(Arrays.asList("tagId1", "tagId2")));

    //add artifact to team
    team.setArtifactIds(Arrays.asList("artifactId"));

    //Initialize DTO
    teamCreateDto = new TeamCreateDto("teamName", "teamDescription",
        "projectId", new ArrayList<>(Arrays.asList("user1@example.com",
        "user2@example.com")));
    completedTeamCreateDto = new CompletedTeamCreateDto("teamName",
        "projectId", 123456789L, "teamDescription",
        Arrays.asList("user1", "user2")
    );

    // Mock SecurityContextHolder to provide an authenticated user
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(owner));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

  }

  /**
   * Tests a successful execution of the addTeam Method
   */
  @Test
  void testAddTeam_success() {
    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(teamRepository.save(any(Team.class))).thenReturn(team);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    when(userRepository.findByUserId("user1Id")).thenReturn(user1);
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
    when(userRepository.findByUserId("user2Id")).thenReturn(user2);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Team added successfully",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the addTeam method where the TeamCreateDto is invalid
   */
  @Test
  void testAddTeam_InvalidDto() {
    // Arrange
    TeamCreateDto invalidDto = new TeamCreateDto("",
        "", "", new ArrayList<>());

    // Act
    ResponseEntity<?> response = teamService.addTeam(invalidDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid team data", ((Map<String, String>)
        response.getBody()).get("msg"));

  }

  /**
   * Tests an execution of the addTeam method where the project is not found
   *
   * @throws TeamValidationException if the project is not found
   */
  @Test
  void testAddTeam_ProjectNotFound() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Project not found", ((Map<String, String>) response.getBody())
        .get("msg"));
  }

  /**
   * Tests an execution of the addTeam method where the user is not the project owner
   *
   * @throws TeamValidationException if the user is not the project owner
   */
  @Test
  void testAddTeam_UserNotProjectOwner() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);

    project.setOwnerId("NotOwnerId");

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Only the project owner can create a team",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the addTeam method where the team name is too short
   *
   * @throws TeamValidationException if the team name is too short
   */
  @Test
  void testAddTeam_NameTooShort() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("aa", "teamDescription",
        "projectId", new ArrayList<>(Arrays.asList("user2@example.com",
        "user3@example.com")));

    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Team name is too short",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * tests an execution of the addTeam method where the team name is too long
   *
   * @throws TeamValidationException if the team name is too long
   */
  @Test
  void testAddTeam_NameTooLong() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("teamNameIsTooLong",
        "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("user2@example.com", "user3@example.com")));

    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Team name is too long",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the addTeam method where the owner email is in the list
   *
   * @throws TeamValidationException if the owner email is in the list
   */
  @Test
  void testAddTeam_OwnerEmailNotInList() throws TeamValidationException {
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription",
        "projectId",
        new ArrayList<>(Arrays.asList("user@example.com", "user1@example.com",
            "user2@example.com")));

    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Owner email must not be in the list",
        ((Map<String, String>) response.getBody()).get("msg"));

  }

  /**
   * Tests an execution of the addTeam method where the team has not enough users
   *
   * @throws TeamValidationException if the team has not enough users
   */
  @Test
  void testAddTeam_NotEnoughUsers() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription",
        "projectId", new ArrayList<>(Arrays.asList("user2@example.com")));

    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("A team must have at least " + TeamConstants.MIN_TEAM_USERS
            + " users",
        ((Map<String, String>) response.getBody()).get("msg"));

  }

  /**
   * Tests an execution of the addTeam method where the team has too many users
   *
   * @throws TeamValidationException if the team has too many users
   */
  @Test
  void testAddTeam_TooManyUsers() throws TeamValidationException {
    //Creation of all the users
    User user3 = new User("user3", "user3@example.com",
        "password", "FirstName3", "LastName3");
    user3.setUserId("user3Id");

    User user4 = new User("user4", "user4@example.com",
        "password", "FirstName4", "LastName4");
    user4.setUserId("user4Id");

    User user5 = new User("user5", "user5@example.com",
        "password", "FirstName5", "LastName5");
    user5.setUserId("user5Id");

    User user6 = new User("user6", "user6@example.com",
        "password", "FirstName6", "LastName6");
    user6.setUserId("user6Id");

    User user7 = new User("user7", "user7@example.com",
        "password", "FirstName7", "LastName7");
    user7.setUserId("user7Id");

    User user8 = new User("user8", "user8@example.com",
        "password", "FirstName8", "LastName8");
    user8.setUserId("user8Id");

    User user9 = new User("user9", "user9@example.com",
        "password", "FirstName9", "LastName9");
    user9.setUserId("user9Id");

    User user10 = new User("user10", "user10@example.com",
        "password", "FirstName10", "LastName10");
    user10.setUserId("user10Id");

    User user11 = new User("user11", "user11@example.com",
        "password", "FirstName11", "LastName11");
    user11.setUserId("user11Id");

    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription",
        "projectId", new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com"
        , "user3@example.com", "user4@example.com", "user5@example.com", "user6@example.com"
        , "user7@example.com", "user8@example.com", "user9@example.com", "user10@example.com",
        "user11@example.com")));

    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
    when(userRepository.findByEmail("user3@example.com")).thenReturn(user3);
    when(userRepository.findByEmail("user4@example.com")).thenReturn(user4);
    when(userRepository.findByEmail("user5@example.com")).thenReturn(user5);
    when(userRepository.findByEmail("user6@example.com")).thenReturn(user6);
    when(userRepository.findByEmail("user7@example.com")).thenReturn(user7);
    when(userRepository.findByEmail("user8@example.com")).thenReturn(user8);
    when(userRepository.findByEmail("user9@example.com")).thenReturn(user9);
    when(userRepository.findByEmail("user10@example.com")).thenReturn(user10);
    when(userRepository.findByEmail("user11@example.com")).thenReturn(user11);
    when(userRepository.findByUserId("user1Id")).thenReturn(user1);
    when(userRepository.findByUserId("user2Id")).thenReturn(user2);
    when(userRepository.findByUserId("user3Id")).thenReturn(user3);
    when(userRepository.findByUserId("user4Id")).thenReturn(user4);
    when(userRepository.findByUserId("user5Id")).thenReturn(user5);
    when(userRepository.findByUserId("user6Id")).thenReturn(user6);
    when(userRepository.findByUserId("user7Id")).thenReturn(user7);
    when(userRepository.findByUserId("user8Id")).thenReturn(user8);
    when(userRepository.findByUserId("user9Id")).thenReturn(user9);
    when(userRepository.findByUserId("user10Id")).thenReturn(user10);
    when(userRepository.findByUserId("user11Id")).thenReturn(user11);

    project.setUserIds(Arrays.asList("ownerId", "user1Id", "user2Id", "user3Id", "user4Id",
        "user5Id", "user6Id", "user7Id", "user8Id", "user9Id", "user10Id", "user11Id"));

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("A team cannot have more than " +
            TeamConstants.MAX_TEAM_USERS + " users",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the addTeam method where there is an empty email in the list
   *
   * @throws TeamValidationException if there is an empty email in the list
   */
  @Test
  void testAddTeam_EmptyEmail() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription",
        "projectId", new ArrayList<>(Arrays.asList("", "user2@example.come")));

    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("There is an empty email in the list. Remove it.",
        ((Map<String, String>) response.getBody()).get("msg"));

  }

  /**
   * Tests an execution of the addTeam method where there is a null email in the list
   */
  @Test
  void testAddTeam_NullEmail() {
    // Arrange
    teamCreateDto = new TeamCreateDto("TeamName", "teamDescription",
        "projectId", new ArrayList<>(Arrays.asList(null, "user2@example.come")));

    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("There is an empty email in the list. Remove it.",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the addTeam method where the email does not exist
   *
   * @throws TeamValidationException if the email does not exist
   */
  @Test
  void addTeam_EmailDoesNotExist() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(null);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User with email user1@example.com does not exist",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the addTeam method where the team description is too long
   *
   * @throws TeamValidationException if the team description is too long
   */
  @Test
  void addTeam_TeamDescriptionTooLong() throws TeamValidationException {
    // Arrange
    teamCreateDto = new TeamCreateDto("teamName",
        "This is a very long team description that is intentionally written to"
            + " exceed the maximum allowed length of 300 characters. The purpose of this "
            + "description is to test the validation logic in the application to ensure that it "
            + "correctly identifies and handles descriptions that are too long. "
            + "By including a variety of words and phrases, we can make sure that the description "
            + "is sufficiently lengthy to trigger the validation error. This should be more than "
            + "enough characters to exceed the limit.",
        "projectId", new ArrayList<>(Arrays.asList("user1@example.com",
        "user2@example.com")));

    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    when(userRepository.findByUserId("user1Id")).thenReturn(user1);
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
    when(userRepository.findByUserId("user2Id")).thenReturn(user2);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Team description is too long. Max is "
            + TeamConstants.MAX_TEAM_DESCRIPTION_LENGTH + " "
            + "characters including whitespaces.",
        ((Map<String, String>) response.getBody()).get("msg"));

  }

  /**
   * Tests an execution of the addTeam method where the team description is empty
   *
   * @throws NoSuchMethodException  if the validateTeam method is not found
   * @throws InvocationTargetException if the validateTeam method cannot be invoked
   * @throws IllegalAccessException  if the validateTeam method cannot be accessed
   */
  @Test
  void testAddTeam_TeamDescriptionEmpty()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //Arrange
    teamCreateDto = new TeamCreateDto("teamName", null,
        "projectId", new ArrayList<>(Arrays.asList("user1@example.com",
        "user2@example.com")));

    //Arrange: Set up mock objects and spy on the service class
    TeamService teamServiceSpy = Mockito.spy(teamService);

    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    when(userRepository.findByUserId("user1Id")).thenReturn(user1);
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
    when(userRepository.findByUserId("user2Id")).thenReturn(user2);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    //Use reflection to invoke the private method 'validateTeam'
    Method validateTeamMethod = TeamService.class.getDeclaredMethod("validateTeam",
        TeamCreateDto.class);
    validateTeamMethod.setAccessible(true); // Make the method accessible

    CompletedTeamCreateDto result = (CompletedTeamCreateDto) validateTeamMethod.
        invoke(teamServiceSpy, teamCreateDto);

    //Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Hi, we are team teamName!", result.teamDescription());
  }

  //TODO: Add test for addTeam method where the team is null

  /**
   * Tests an execution of the addTeam method where the team is null
   *
   * @throws TeamValidationException if the team is null
   */
  @Test
  void testAddTeam_NullTeam() throws TeamValidationException {
  }

  //TODO: Add test for addTeam method where user is null
  //TODO: Add test for addTeam method where user is empty

  /**
   * Tests an execution of the addTeam method where the user does not exist in the database
   *
   * @throws TeamValidationException if the user does not exist
   */
  @Test
  void testAddTeam_UserIdDoesNotExist() throws TeamValidationException {
    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(teamRepository.save(any(Team.class))).thenReturn(team);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    when(userRepository.findByUserId("user1Id")).thenReturn(null);
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
    when(userRepository.findByEmail("user2Id")).thenReturn(user2);

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User with ID user1Id does not exist",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  //TODO: Add test for when an user is the team is already in an another team

  /**
   * Tests the addTeam method where the user is not part of the project
   */
  @Test
  void testAddTeam_UserIsNotPartOfProject() {
    // Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(teamRepository.save(any(Team.class))).thenReturn(team);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    when(userRepository.findByUserId("user1Id")).thenReturn(user1);
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
    when(userRepository.findByEmail("user2Id")).thenReturn(user2);

    project.setUserIds(Arrays.asList("ownerId", "user2Id"));

    // Act
    ResponseEntity<?> response = teamService.addTeam(teamCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User with ID user1Id is not part of the project",
        ((Map<String, String>) response.getBody()).get("msg"));
  }


  /**
   * Tests and AddTeam execution by using reflection to see if the CompletedTeamCreateDto is created
   * correctly
   *
   * @throws NoSuchMethodException     if the validateTeam method is not found
   * @throws InvocationTargetException if the validateTeam method cannot be invoked
   * @throws IllegalAccessException    if the validateTeam method cannot be accessed
   */
  @Test
  void testAddTeam_success_usingReflection()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Arrange: Set up mock objects and spy on TeamService
    TeamService teamServiceSpy = Mockito.spy(teamService);

    // Prepare DTO
    TeamCreateDto teamCreateDto = new TeamCreateDto("teamName",
        "teamDescription", "projectId",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    // Mock necessary repository calls
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(userRepository.findByUserId("ownerId")).thenReturn(owner);
    when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);

    // Prepare the expected CompletedTeamCreateDto
    CompletedTeamCreateDto expectedDto = new CompletedTeamCreateDto("teamName",
        "projectId", 123456789L,
        "teamDescription", Arrays.asList("user1Id", "user2Id"));

    // Use reflection to invoke the private method 'validateTeam'
    Method validateTeamMethod = TeamService.class.getDeclaredMethod("validateTeam",
        TeamCreateDto.class);
    validateTeamMethod.setAccessible(true); // Make the method accessible

    // Act: Invoke the private method
    CompletedTeamCreateDto result = (CompletedTeamCreateDto) validateTeamMethod.
        invoke(teamServiceSpy, teamCreateDto);

    // Assert: Check if 'creationDate' is non-negative and if userIds match
    assertTrue(result.creationDate() >= 0); // Ensure creationDate is valid (non-negative)
    assertEquals(expectedDto.teamName(), result.teamName());
    assertEquals(expectedDto.projectId(), result.projectId());
    assertEquals(expectedDto.teamDescription(), result.teamDescription());
    assertEquals(expectedDto.userIds(), result.userIds());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the team i null
   *
   * @throws NoSuchMethodException     if the addTeamToUsers method is not found
   * @throws InvocationTargetException if the addTeamToUsers method cannot be invoked
   * @throws IllegalAccessException    if the addTeamToUsers method cannot be accessed
   */
  @Test
  void testAddTeamToUsers_TeamNull() throws NoSuchMethodException,
      InvocationTargetException, IllegalAccessException {
    //Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);
    team = null;

    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod = TeamService.class.getDeclaredMethod("addTeamToUsers",
          Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    //Assert
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause,
        "Expected a TeamValidationException.");
    assertEquals("Team cannot be null", cause.getMessage());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the team has a null user
   *
   * @throws NoSuchMethodException  if the addTeamToUsers method is not found
   * @throws IllegalAccessException if the addTeamToUsers method cannot be accessed
   */
  @Test
  void testAddTeamToUsers_UserIdNull()
      throws NoSuchMethodException, IllegalAccessException {
    // Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);

    team.setUserIds(Arrays.asList(null, "user1Id"));

    // Act & Assert
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod = TeamService.class.getDeclaredMethod(
          "addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert the cause
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause,
        "Expected a TeamValidationException.");
    assertEquals("User ID is null or empty", cause.getMessage());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the team has an empty user
   *
   * @throws NoSuchMethodException  if the addTeamToUsers method is not found
   * @throws IllegalAccessException if the addTeamToUsers method cannot be accessed
   */
  @Test
  void testAddTeamToUsers_UserIdEmpty()
      throws NoSuchMethodException, IllegalAccessException {
    // Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);

    team.setUserIds(Arrays.asList("", "user1Id"));

    // Act & Assert
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod = TeamService.class.getDeclaredMethod(
          "addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert the cause
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause,
        "Expected a TeamValidationException.");
    assertEquals("User ID is null or empty", cause.getMessage());
  }


  /**
   * Tests the method addTeamToUsers with reflection, in the case where the user does not exist
   *
   * @throws NoSuchMethodException  if the addTeamToUsers method is not found
   * @throws IllegalAccessException if the addTeamToUsers method cannot be accessed
   */
  @Test
  void testAddTeamToUsers_UserDoesNotExist() throws NoSuchMethodException, IllegalAccessException {
    // Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);

    team.setUserIds(Arrays.asList("nonexistentUser", "user1Id"));
    when(userRepository.findByUserId("nonexistentUser")).thenReturn(null);
    when(userRepository.findByUserId("user1")).thenReturn(user1);

    // Act & Assert
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod = TeamService.class.getDeclaredMethod(
          "addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    // Assert the cause
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause,
        "Expected a TeamValidationException.");
    assertEquals("User with ID nonexistentUser does not exist", cause.getMessage());
  }

  /**
   * Tests the method addTeamToUsers with reflection, in the case where the user is not part of the
   * project
   *
   * @throws NoSuchMethodException  if the addTeamToUsers method is not found
   * @throws IllegalAccessException if the addTeamToUsers method cannot be accessed
   */
  @Test
  void testAddTeamToUsers_UserNotInProject() throws NoSuchMethodException, IllegalAccessException {
    //Arrange
    TeamService teamServiceSpy = Mockito.spy(teamService);

    team.setProjectId("projectId");
    when(userRepository.findByUserId("user1Id")).thenReturn(user1);
    when(userRepository.findByUserId("user2Id")).thenReturn(user2);

    project.setUserIds(Arrays.asList("user1Id", "user3Id")); // User not in project
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);

    //Act
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method addTeamToUsersMethod = TeamService.class.getDeclaredMethod
          ("addTeamToUsers", Team.class);
      addTeamToUsersMethod.setAccessible(true);
      addTeamToUsersMethod.invoke(teamServiceSpy, team);
    });

    //Assert
    Throwable cause = exception.getCause();
    assertNotNull(cause, "Expected an exception cause but found null.");
    assertInstanceOf(TeamValidationException.class, cause,
        "Expected a TeamValidationException.");
    assertEquals("User with ID user2Id is not part of the project",
        cause.getMessage());
  }

  /**
   * Tests a successful execution of the  getTeamsByProject method
   */
  @Test
  void testGetTeamsByProject() {
    //Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(teamRepository.existsByProjectId("projectId")).thenReturn(true);
    when(teamRepository.findTeamsByProjectId("projectId")).thenReturn(Arrays.asList(team));

    //Act
    ResponseEntity<?> response = teamService.getTeamsByProject("projectId");

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Arrays.asList(team), response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByProject method where the project is null
   */
  @Test
  void testGetTeamsByProject_ProjectNull() {
    //Act
    ResponseEntity<?> response = teamService.getTeamsByProject(null);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Project ID is null or empty", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByProject method where the project is empty
   */
  @Test
  void testGetTeamsByProject_ProjectEmpty() {
    //Act
    ResponseEntity<?> response = teamService.getTeamsByProject("");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Project ID is null or empty", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByProject method where there no teams found for the project
   */
  @Test
  void testGetTeamsByProject_NoTeamsFound() {
    //Arrange
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(teamRepository.existsByProjectId("projectId")).thenReturn(false);

    //Act
    ResponseEntity<?> response = teamService.getTeamsByProject("projectId");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No teams found for project ID projectId", response.getBody());
  }

  /**
   * Tests a successful execution of the getTeamByUser method
   */
  @Test
  void testGetTeamsByUser() {
    //Arrange
    when(userRepository.findByUserId("userId")).thenReturn(user1);
    when(teamRepository.existsByUserIdsContaining("userId")).thenReturn(true);
    when(teamRepository.findByUserIdsContaining("userId")).thenReturn(Arrays.asList(team));

    //Act
    ResponseEntity<?> response = teamService.getTeamsByUser("userId");

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Arrays.asList(team), response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByUser method where the user is null
   */
  @Test
  void testGetTeamsByUser_UserNull() {
    //Act
    ResponseEntity<?> response = teamService.getTeamsByUser(null);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User ID is null or empty", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByUser method where the user is empty
   */
  @Test
  void testGetTeamsByUser_UserEmpty() {
    //Act
    ResponseEntity<?> response = teamService.getTeamsByUser("");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User ID is null or empty", response.getBody());
  }

  /**
   * Tests an execution of the getTeamsByUser method where there are no teams found for the user
   */
  @Test
  void testGetTeamsByUser_NoTeamsFound() {
    //Arrange
    when(userRepository.findByUserId("userId")).thenReturn(user1);
    when(teamRepository.existsByUserIdsContaining("userId")).thenReturn(false);

    //Act
    ResponseEntity<?> response = teamService.getTeamsByUser("userId");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No teams found for user ID userId", response.getBody());
  }

  /**
   * Tests a successful execution of the deleteTeam method
   */
  @Test
  void testDeleteTeam() {
    //Arrange
    when(teamRepository.findById("teamId")).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(project);

    when(userRepository.findById("user2Id")).thenReturn(Optional.ofNullable(user2));
    when(userRepository.save(user2)).thenReturn(user2);
    when(userRepository.findById("user1Id")).thenReturn(Optional.ofNullable(user1));
    when(userRepository.save(user1)).thenReturn(user1);
    when(teamRepository.existsById("teamId")).thenReturn(false);

    when(artifactService.deleteArtifact("artifactId")).
        thenReturn(ResponseEntity.ok().build());

    //Act
    ResponseEntity<?> response = teamService.deleteTeam("teamId");

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team deleted successfully.");
    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTeam method where the team is null
   */
  @Test
  void testDeleteTeam_TeamNull() {
    //Act
    ResponseEntity<?> response = teamService.deleteTeam(null);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Team ID is null or empty",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the deleteTeam method where the team is empty
   */
  @Test
  void testDeleteTeam_TeamEmpty() {
    //Act
    ResponseEntity<?> response = teamService.deleteTeam("");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Team ID is null or empty",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the deleteTeam method where the team is not found
   */
  @Test
  void testDeleteTeam_TeamNotFound() {
    //Arrange
    when(teamRepository.findById("teamId")).thenReturn(Optional.empty());

    //Act
    ResponseEntity<?> response = teamService.deleteTeam("teamId");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Team not found",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Test an execution of the deleteTeam method where the project is not found
   */
  @Test
  void testDeleteTeam_ProjectNotFound() {
    //Arrange
    when(teamRepository.findById("teamId")).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(null);

    //Act
    ResponseEntity<?> response = teamService.deleteTeam("teamId");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Project not found",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the deleteTeam method where the user is not found
   */
  @Test
  void testDeleteTeam_UserNotFound() {
    //Arrange
    when(teamRepository.findById("teamId")).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(project);

    when(userRepository.findById("user1Id")).thenReturn(Optional.ofNullable(user1));
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.findById("user2Id")).thenReturn(Optional.empty());

    //Act
    ResponseEntity<?> response = teamService.deleteTeam("teamId");

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User with ID user2Id not found",
        ((Map<String, String>) response.getBody()).get("msg"));
  }

  /**
   * Tests an execution of the deleteTeam method where the team is not deleted
   */
  @Test
  void testDeleteTeam_TeamNotDeleted() {
    //Arrange
    when(teamRepository.findById("teamId")).thenReturn(Optional.ofNullable(team));
    when(projectRepository.findProjectByProjectId("projectId")).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(project);

    when(userRepository.findById("user2Id")).thenReturn(Optional.ofNullable(user2));
    when(userRepository.save(user2)).thenReturn(user2);
    when(userRepository.findById("user1Id")).thenReturn(Optional.ofNullable(user1));
    when(userRepository.save(user1)).thenReturn(user1);
    when(teamRepository.existsById("teamId")).thenReturn(true);

    when(artifactService.deleteArtifact("artifactId")).
        thenReturn(ResponseEntity.ok().build());

    //Act
    ResponseEntity<?> response = teamService.deleteTeam("teamId");
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Team not deleted.");

    //Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getTeamIrr method
   */
  @Test
  void testGetTeamIrr_Success() {
    //Arrange
    when(teamRepository.findTeamByTeamId("teamId")).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId("artifactId")).thenReturn(artifact);
    when(tagRepository.findTagByTagId("tag1Id")).thenReturn(tag1);
    when(tagRepository.findTagByTagId("tag2Id")).thenReturn(tag2);

    String mockAlphaResponse = "{\"alpha\": 0.85}";
    when(pythonClientService.getKrippendorffAlpha(Mockito.anyList())).thenReturn(mockAlphaResponse);

    //Act
    ResponseEntity<?> response = teamService.getTeamIrr("teamId");

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertNotNull(responseBody);
    assertEquals("Successfully retrieved Krippendorff's alpha", responseBody.get("msg"));
    assertEquals(0.85, responseBody.get("irr"));
  }

  /**
   * Tests an execution of the getTeamIrr method where the team is not found
   */
  @Test
  void testGetTeamIrr_TeamNotFound() {
    //Arrange
    when(teamRepository.findTeamByTeamId("teamId")).thenReturn(null);

    //Act
    ResponseEntity<?> response = teamService.getTeamIrr("teamId");

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertNotNull(responseBody);
    assertEquals("Team not found", responseBody.get("msg"));
  }

  /**
   * Tests an execution of the getTeamIrr method where the artifact is not found
   */
  @Test
  void testGetTeamIrr_ArtifactNotFound() {
    //Arrange
    when(teamRepository.findTeamByTeamId("teamId")).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId("artifactId")).thenReturn(null);

    //Act
    ResponseEntity<?> response = teamService.getTeamIrr("teamId");

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertNotNull(responseBody);
    assertEquals("Artifact not found", responseBody.get("msg"));
  }

  /**
   * Tests an execution of the getTeamIrr method where the tag is not found
   */
  @Test
  void testGetTeamIrr_NoTagsForArtifact() {
    //Arrange
    artifact.setTags(new ArrayList<>()); // Artifact has no tags
    when(teamRepository.findTeamByTeamId("teamId")).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId("artifactId")).thenReturn(artifact);

    String mockAlphaResponse = "{\"alpha\": 1.0}";
    when(pythonClientService.getKrippendorffAlpha(Mockito.anyList())).thenReturn(mockAlphaResponse);

    //Act
    ResponseEntity<?> response = teamService.getTeamIrr("teamId");

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertNotNull(responseBody);
    assertEquals("Successfully retrieved Krippendorff's alpha", responseBody.get("msg"));
    assertEquals(1.0, responseBody.get("irr")); // Default alpha value
  }

  /**
   * Tests an execution of the getTeamIrr method where the tag creator is mismatched
   */
  @Test
  void testGetTeamIrr_TagCreatedByUserMismatch() {
    //Arrange
    when(teamRepository.findTeamByTeamId("teamId")).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId("artifactId")).thenReturn(artifact);

    tag1.setCreatedBy("otherUserId"); // No user in the team matches this creator
    when(tagRepository.findTagByTagId("tagId1")).thenReturn(tag1);
    when(tagRepository.findTagByTagId("tagId2")).thenReturn(tag2);

    String mockAlphaResponse = "{\"alpha\": 0.0}";
    when(pythonClientService.getKrippendorffAlpha(Mockito.anyList())).thenReturn(mockAlphaResponse);

    //Act
    ResponseEntity<?> response = teamService.getTeamIrr("teamId");

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertNotNull(responseBody);
    assertEquals("Successfully retrieved Krippendorff's alpha", responseBody.get("msg"));
    assertEquals(0.0, responseBody.get("irr")); // Alpha drops due to mismatch
  }

  /**
   * Test a successful execution of UpdateTeamUsers method
   */
  //@Test
  //void testUpdateTeamUsers_Success() {
    // Arrange
    //when(teamRepository.findTeamByTeamId("teamId")).thenReturn(team);
    //when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
    //when(userRepository.findByEmail("user3@example.com")).thenReturn(user3);
    //when(userRepository.findByUserId("user1Id")).thenReturn(user1);
    //when(userRepository.findByUserId("user3Id")).thenReturn(user3);
    //when(userRepository.save(user3)).thenReturn(user3);

    //List<String> userEmails = Arrays.asList("user1@example.com", "user3@example.com");

    // Act
    //ResponseEntity<?> response = teamService.updateTeamUsers("teamId", userEmails);

    // Assert
    //assertNotNull(response);
    //assertEquals(HttpStatus.OK, response.getStatusCode());
    //assertEquals("Team users updated successfully",
    //    ((Map<String, String>) response.getBody()).get("msg"));
    //verify(teamRepository).save(team);
    //assertEquals(Arrays.asList("user1Id", "user3Id"), team.getUserIds());
    //assertTrue(user3.getTeamIds().contains("teamId"));
  //}


}

