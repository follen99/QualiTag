package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.constants.ProjectConstants;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectInfoDto;
import it.unisannio.studenti.qualitag.dto.project.WholeProjectDto;
import it.unisannio.studenti.qualitag.dto.project.WholeProjectHeavyDto;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
import it.unisannio.studenti.qualitag.dto.user.UserResponseDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
import it.unisannio.studenti.qualitag.exception.ProjectValidationException;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.mapper.TeamMapper;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.ProjectStatus;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests the ProjectService class.
 */
public class ProjectServiceTest {

  @Mock
  private ArtifactService artifactService;
  @Mock
  private TeamService teamService;

  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private ArtifactRepository artifactRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private TeamRepository teamRepository;

  @InjectMocks
  private ProjectService projectService;

  private ProjectCreateDto projectCreateDto;
  private ProjectCreateDto projectModifyDto;

  private Artifact artifact1;
  private Artifact artifact2;

  private Project project;

  private User user1;
  private User user2;
  private User owner;
  private User otherUser;

  private Team team;

  private Tag tag1;
  private Tag tag2;
  private Tag tag3;
  private Tag tag4;


  /**
   * Sets up the test environment.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Initialize the objects
    tag1 = new Tag("TAG1", "6798e2740b80b85362a8ba90", "#fff8de");
    tag1.setTagId("6744ba6c60e0564864250e89");
    tag2 = new Tag("TAG2", "6798e2740b80b85362a8ba90", "#295f98");
    tag2.setTagId("6755b79afc22f97c06a34275");
    tag3 = new Tag("TAG3", "6798e2740b80b85362a8ba90", "#295f98");
    tag3.setTagId("6755b79afc22f97c06a34276");
    tag4 = new Tag("TAG4", "6798e2740b80b85362a8ba90", "#295f98");
    tag4.setTagId("6755b79afc22f97c06a34277");

    user1 = new User("user1", "user1@example.com", "password1", "Jane", "Doe");
    user1.setUserId("6798e2740b80b85362a8ba90");
    user1.setTagIds(new ArrayList<>(Arrays.asList(tag1.getTagId(), tag2.getTagId())));
    user2 = new User("user2", "user2@example.com", "password2", "John", "Doe");
    user2.setUserId("6798e6740b70b85362a8ba91");
    owner = new User("owner", "owner@example.com", "password3", "Alice", "Smith");
    owner.setUserId("6998e2740b87d85362a8ba58");
    otherUser = new User("otherUser", "other@example.com", "password", "Bob", "Johnson");
    otherUser.setUserId("6798e2740b80b85362a8ba92");

    artifact1 = new Artifact("artifact1Name", "projectId", "teamId", "filePath1");
    artifact1.setArtifactId("6754705c8d6446369ca02b62");
    artifact1.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId(), tag2.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact1.getArtifactId())));
    tag2.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact1.getArtifactId())));
    artifact2 = new Artifact("artifact2Name", "projectId", "teamId", "filePath2");
    artifact2.setArtifactId("6754705c8d6446369ca02b63");
    artifact2
        .setTags(new ArrayList<>(Arrays.asList(tag1.getTagId(), tag3.getTagId(), tag4.getTagId())));
    tag1.setArtifactIds(
        new ArrayList<>(Arrays.asList(artifact1.getArtifactId(), artifact2.getArtifactId())));
    tag3.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact2.getArtifactId())));
    tag4.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact2.getArtifactId())));

    long creationDate = Instant.now().toEpochMilli();
    long deadline = Instant.parse("2025-12-31T23:59:59Z").toEpochMilli();
    project = new Project("projectName", "projectDescription", creationDate, deadline,
        "6998e2740b87d85362a8ba58", new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("6998e2740b87d85362a8ba58",
        "6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    user1.setProjectIds(new ArrayList<>(List.of("projectId")));
    owner.setProjectIds(new ArrayList<>(List.of("projectId")));
    project.setTeamIds(new ArrayList<>(List.of("teamId")));
    project.setArtifactIds(
        new ArrayList<>(Arrays.asList(artifact1.getArtifactId(), artifact2.getArtifactId())));

    team = new Team("teamName", "projectId", 123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(List.of("teamId")));
    user2.setTeamIds(new ArrayList<>(List.of("teamId")));

    // Initialize the DTO
    projectCreateDto = new ProjectCreateDto("projectName", "projectDescription",
        "2025-12-31T23:59:59Z", new ArrayList<>(Arrays.asList(user1.getEmail(), user2.getEmail())));
    projectModifyDto =
        new ProjectCreateDto("projectNewName", "projectNewDescription", "2026-12-31T23:59:59Z",
            new ArrayList<>(Arrays.asList(user1.getEmail(), otherUser.getEmail())));

    // Initialize authorization details
    // Mock SecurityContextHolder to provide an authenticated user
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("owner");
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(owner));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  /**
   * Test a successful execution of the createProject method.
   */
  @Test
  public void testCreateProjectSuccess() {
    // Arrange
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamService.addTeam(any(TeamCreateDto.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

    // Mock static method before invoking validateProject
    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class);
        MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class, (mock,
            context) -> doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      mockedMapper.when(() -> ProjectMapper.toEntity(any(CompletedProjectCreationDto.class)))
          .thenReturn(project); // Ensure this runs before validateProject

      // Act
      ResponseEntity<?> response = projectService.createProject(projectCreateDto);

      // Assert
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Project created successfully.");
      responseBody.put("projectId", project.getProjectId());
      assertEquals(responseBody, response.getBody());
      verify(projectRepository).save(project);
    }
  }

  /**
   * Test an execution of the createProject method with an invalidDto.
   */
  @Test
  public void testCreateProjectInvalidDto() {
    // Arrange
    ProjectCreateDto invalidDto = new ProjectCreateDto("", "", "", new ArrayList<>());

    // Act
    ResponseEntity<?> response = projectService.createProject(invalidDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the createProject method with an existing project name.
   */
  @Test
  public void testCreateProjectExistingProjectName() {
    // Arrange
    when(projectRepository.existsByProjectName("projectName")).thenReturn(true);

    // Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project with name 'projectName' already exists.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the createProject method with the deadline in the past.
   */
  @Test
  public void testCreateProjectPastDeadline() {
    // Arrange
    ProjectCreateDto pastDeadlineDto =
        new ProjectCreateDto("projectName", "projectDescription", "2020-12-31T23:59:59Z",
            new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    // Act
    ResponseEntity<?> response = projectService.createProject(pastDeadlineDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be before the creation date.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the createProject method with the deadline to far in the future.
   */
  @Test
  public void testCreateProjectTooFarDeadline() {
    // Arrange
    ProjectCreateDto futureDeadlineDto =
        new ProjectCreateDto("projectName", "projectDescription", "2035-12-31T23:59:59Z",
            new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    // Act
    ResponseEntity<?> response = projectService.createProject(futureDeadlineDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be after "
        + ProjectConstants.MAX_PROJECT_DEADLINE_YEARS + " years from now.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner with an empty id.
   */
  @Test
  public void testCreateProjectOwnerIdEmpty() {
    // Arrange
    owner.setUserId("");

    // Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner with a null id.
   */
  @Test
  public void testCreateProjectOwnerIdNull() {
    // Arrange
    owner.setUserId(null);

    // Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner that does not exist.
   */
  @Test
  public void testCreateProjectOwnerNotFound() {
    // Arrange
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + owner.getUserId() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner that's part of the email list.
   */
  @Test
  public void testCreateProjectOwnerInEmailList() {
    // Arrange
    ProjectCreateDto ownerEmailDto = new ProjectCreateDto("projectName", "projectDescription",
        "2025-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com", owner.getEmail())));

    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = projectService.createProject(ownerEmailDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner must not be part of the list of users.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when there's an empty email in the list.
   */
  @Test
  public void testCreateProjectEmptyEmail() {
    // Arrange
    ProjectCreateDto emptyEmailDto = new ProjectCreateDto("projectName", "projectDescription",
        "2025-12-31T23:59:59Z", new ArrayList<>(Arrays.asList(user1.getEmail(), null)));
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    // Act
    ResponseEntity<?> response = projectService.createProject(emptyEmailDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "There is an empty email in the list. Please remove it.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when there's a repeated email in the list.
   */
  @Test
  public void testCreateProjectRepeatedEmail() {
    // Arrange
    ProjectCreateDto repeatedEmailDto = new ProjectCreateDto("projectName", "projectDescription",
        "2025-12-31T23:59:59Z", new ArrayList<>(Arrays.asList(user1.getEmail(), user1.getEmail())));
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response = projectService.createProject(repeatedEmailDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg",
        "User with email " + user1.getEmail() + " is mentioned more than once.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when a user in the list does not exist.
   */
  @Test
  public void testCreateProjectUserNotFound() {
    // Arrange
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with email " + user1.getEmail() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when the team creation fails.
   */
  @Test
  public void testCreateProjectFailureTeamCreation() {
    // Arrange
    // We use a spy so we test later if the deleteProject method was called
    ProjectService projectServiceSpy = Mockito.spy(projectService);
    when(projectRepository.existsByProjectName("projectName")).thenReturn(false);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Simulate team creation failure (BAD_REQUEST instead of CREATED)
    when(teamService.addTeam(any(TeamCreateDto.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    // Mock the static method
    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class)) {
      mockedMapper.when(() -> ProjectMapper.toEntity(any(CompletedProjectCreationDto.class)))
          .thenReturn(project);

      // Act
      ResponseEntity<?> response = projectServiceSpy.createProject(projectCreateDto);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      // Verify that the project was saved initially
      verify(projectRepository).save(any(Project.class));
      // Verify that deleteProject was called to roll back the project
      verify(projectServiceSpy, times(1)).deleteProject(project.getProjectId());
    }
  }

  /**
   * Tests an execution of the createProject method when the addProjectToUser method fails.
   */
  @Test
  public void testCreateProjectFailureAddProjectToUser() {
    // Arrange
    // Use a spy so we test later if the deleteProject method was called
    ProjectService projectServiceSpy = Mockito.spy(projectService);
    // Set the project ownerId to null, so that the addProjectToUser method fails
    project.setOwnerId("otherUserId");
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamService.addTeam(any(TeamCreateDto.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

    // Mock static method before invoking validateProject
    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class);
        MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class, (mock,
            context) -> doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      mockedMapper.when(() -> ProjectMapper.toEntity(any(CompletedProjectCreationDto.class)))
          .thenReturn(project);

      // Act
      ResponseEntity<?> response = projectServiceSpy.createProject(projectCreateDto);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "User with ID otherUserId not found.");
      assertEquals(responseBody, response.getBody());
      verify(projectRepository).save(project);
      verify(projectServiceSpy, times(1)).deleteProject(project.getProjectId());
    }

  }

  /**
   * Tests a successful execution of the updateProject method.
   */
  @Test
  public void testUpdateProjectSuccess() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);
    when(userRepository.existsByEmail(otherUser.getEmail())).thenReturn(true);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(otherUser.getUserId())).thenReturn(otherUser);
    when(teamRepository.findTeamsByProjectId(project.getProjectId()))
        .thenReturn(new ArrayList<>(Collections.singletonList(team)));
    when(teamRepository.save(team)).thenReturn(team);
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.save(user2)).thenReturn(user2);
    when(userRepository.save(otherUser)).thenReturn(otherUser);
    when(projectRepository.save(project)).thenReturn(project);

    try (MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class, (mock,
        context) -> doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      // Act
      ResponseEntity<?> response =
          projectService.updateProject(projectModifyDto, project.getProjectId());

      System.out.println(response.getBody());

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Project updated successfully.");
      assertEquals(responseBody, response.getBody());
      verify(projectRepository).save(project);
      assertEquals("projectNewName", project.getProjectName());
      assertEquals("projectNewDescription", project.getProjectDescription());
      long expectedDeadline = Instant.parse("2026-12-31T23:59:59Z").toEpochMilli();
      assertEquals(expectedDeadline, project.getProjectDeadline());
      assertEquals(new ArrayList<>(Arrays.asList(user1.getUserId(), otherUser.getUserId())),
          project.getUserIds());
      assertTrue(user1.getProjectIds().contains(project.getProjectId()));
      assertTrue(otherUser.getProjectIds().contains(project.getProjectId()));
      assertFalse(user2.getProjectIds().contains(project.getProjectId()));
    }
  }

  /**
   * Tests an execution of the updateProject method with a null project id.
   */
  @Test
  public void testUpdateProjectNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto, null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with an empty project id.
   */
  @Test
  public void testUpdateProjectEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto, "");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with a project id that does not exist.
   */
  @Test
  public void testUpdateProjectProjectIdNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(projectModifyDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when a user that is not the owner tries to
   * update the project.
   */
  @Test
  public void testUpdateProjectNotOwner() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    project.setOwnerId("otherUserId");

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(projectModifyDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Only the owner can modify the project!");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with an invalidDto.
   */
  @Test
  public void testUpdateProjectInvalidDto() {
    // Arrange
    ProjectCreateDto invalidDto = new ProjectCreateDto("", "", "", new ArrayList<>());
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.updateProject(invalidDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with an existing project name.
   */
  @Test
  public void testUpdateProjectExistingProjectName() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.existsByProjectName("projectNewName")).thenReturn(true);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(projectModifyDto, project.getProjectId());

    System.out.println(response.getBody());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project with name projectNewName already exists.");
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests an execution of the updateProject method with the deadline in the past.
   */
  @Test
  public void testUpdateProjectPastDeadline() {
    // Arrange
    ProjectCreateDto pastDeadlineDto =
        new ProjectCreateDto("projectNewName", "projectNewDescription", "2020-12-31T23:59:59Z",
            new ArrayList<>(Arrays.asList(user1.getEmail(), owner.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(pastDeadlineDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be before the creation date.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with the deadline too far in the future.
   */
  @Test
  public void testUpdateProjectTooFarDeadline() {
    // Arrange
    ProjectCreateDto futureDeadlineDto =
        new ProjectCreateDto("projectNewName", "projectNewDescription", "2035-12-31T23:59:59Z",
            new ArrayList<>(Arrays.asList(user1.getEmail(), otherUser.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(futureDeadlineDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be after "
        + ProjectConstants.MAX_PROJECT_DEADLINE_YEARS + " years from now.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when the owner is part of the user list.
   */
  @Test
  public void testUpdateProjectOwnerInUserList() {
    // Arrange
    ProjectCreateDto ownerInUserListDto =
        new ProjectCreateDto("projectNewName", "projectNewDescription", "2026-12-31T23:59:59Z",
            new ArrayList<>(Arrays.asList(user1.getEmail(), owner.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(ownerInUserListDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner must not be part of the list of users.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when there's an empty email in the list.
   */
  @Test
  public void testUpdateProjectEmptyEmail() {
    // Arrange
    ProjectCreateDto emptyEmailDto = new ProjectCreateDto("projectNewName", "projectNewDescription",
        "2026-12-31T23:59:59Z", new ArrayList<>(Arrays.asList(user1.getEmail(), null)));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(emptyEmailDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "There is an empty email in the list. Please remove it.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when there's a repeated email in the list.
   */
  @Test
  public void testUpdateProjectRepeatedEmail() {
    // Arrange
    ProjectCreateDto repeatedEmailDto =
        new ProjectCreateDto("projectNewName", "projectNewDescription", "2026-12-31T23:59:59Z",
            new ArrayList<>(Arrays.asList(user1.getEmail(), user1.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(repeatedEmailDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg",
        "User with email " + user1.getEmail() + " is mentioned more than once.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when an email in the list is not found.
   */
  @Test
  public void testUpdateProjectUserNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(projectModifyDto, project.getProjectId());

    // Assert
    List<String> emailsNotFound =
        new ArrayList<>(Arrays.asList(user1.getEmail(), otherUser.getEmail()));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "The following emails are not registered: " + emailsNotFound);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method one emails in the list is not found.
   */
  @Test
  public void testUpdateProjectOneUserNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.existsByEmail(otherUser.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(projectModifyDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with email " + otherUser.getEmail() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when the team update fails.
   *
   * @throws NullPointerException since the team is not found
   */
  @Test
  public void testUpdateProjecttestUpdateFail() throws NullPointerException {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.existsByEmail(otherUser.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);
    when(teamRepository.findTeamsByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        projectService.updateProject(projectModifyDto, project.getProjectId());

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg",
        "Cannot invoke \"java.util.List.iterator()\" because \"teams\" is null");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getHumanReadableProjectStatus method.
   */
  @Test
  public void testGetHumanReadableProjectStatusSuccess() {
    // Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId(artifact1.getArtifactId()))
        .thenReturn(artifact1);
    when(artifactRepository.findArtifactByArtifactId(artifact2.getArtifactId()))
        .thenReturn(artifact2);

    // Create the expected WholeProjectHeavyDto
    List<UserResponseDto> shortResponseUserDto = new ArrayList<>();
    shortResponseUserDto.add(UserMapper.toUserResponseDto(owner));
    shortResponseUserDto.add(UserMapper.toUserResponseDto(user1));
    shortResponseUserDto.add(UserMapper.toUserResponseDto(user2));

    List<WholeTeamDto> wholeTeamDto = new ArrayList<>();
    wholeTeamDto.add(TeamMapper.toWholeTeamDto(team));

    List<WholeArtifactDto> wholeArtifactDto = new ArrayList<>();
    wholeArtifactDto.add(ArtifactMapper.toWholeArtifactDto(artifact1));
    wholeArtifactDto.add(ArtifactMapper.toWholeArtifactDto(artifact2));

    UserShortResponseDto ownerDto = UserMapper.toUserShortResponseDto(owner);

    WholeProjectHeavyDto expectedWholeProjectHeavyDto = new WholeProjectHeavyDto(
        project.getProjectId(), project.getProjectName(), project.getProjectDescription(),
        project.getProjectCreationDate(), project.getProjectDeadline(), ownerDto,
        project.getProjectStatus().name(), shortResponseUserDto, wholeArtifactDto, wholeTeamDto);

    // Act
    ResponseEntity<?> response =
        projectService.getHumanReadableProjectStatus(project.getProjectId());

    System.out.println(response.getBody());

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedWholeProjectHeavyDto, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method with a null project id.
   */
  @Test
  public void testGetHumanReadableProjectStatusNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getHumanReadableProjectStatus(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method with an empty project id.
   */
  @Test
  public void testGetHumanReadableProjectStatusEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getHumanReadableProjectStatus("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method with a project id that does not
   * exist.
   */
  @Test
  public void testGetHumanReadableProjectStatusProjectIdNotFound() {
    // Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(false);

    // Act
    ResponseEntity<?> response =
        projectService.getHumanReadableProjectStatus(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when the project is not found.
   */
  @Test
  public void testGetHumanReadableProjectStatusProjectNotFound() {
    // Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        projectService.getHumanReadableProjectStatus(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when a user is not found.
   */
  @Test
  public void testGetHumanReadableProjectStatusUserNotFound() {
    // Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    // Act
    ResponseEntity<?> response =
        projectService.getHumanReadableProjectStatus(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + user1.getUserId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when a team is not found.
   */
  @Test
  public void testGetHumanReadableProjectStatusTeamNotFound() {
    // Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        projectService.getHumanReadableProjectStatus(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team with ID " + team.getTeamId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when an artifact is not found.
   */
  @Test
  public void testGetHumanReadableProjectStatusArtifactNotFound() {
    // Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId(artifact1.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        projectService.getHumanReadableProjectStatus(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact with ID " + artifact1.getArtifactId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when the project owner is not
   * found.
   */
  @Test
  public void testGetHumanReadableProjectStatusOwnerNotFound() {
    // Arrange
    project.setOwnerId(otherUser.getUserId());
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId(artifact1.getArtifactId()))
        .thenReturn(artifact1);
    when(artifactRepository.findArtifactByArtifactId(artifact2.getArtifactId()))
        .thenReturn(artifact2);

    // Act
    ResponseEntity<?> response =
        projectService.getHumanReadableProjectStatus(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests the getProjectsByOwner method.
   */
  @Test
  public void testGetProjectsByOwner() {
    // Arrange
    when(userRepository.existsById(owner.getUserId())).thenReturn(true);
    when(projectRepository.findProjectsByOwnerId(owner.getUserId()))
        .thenReturn(new ArrayList<>(Collections.singletonList(project)));

    // Act
    ResponseEntity<?> response = projectService.getProjectsByOwner(owner.getUserId());

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Projects found successfully.");
    responseBody.put("projects", new ArrayList<>(Collections.singletonList(project)));
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsByOwner method with a null owner id.
   */
  @Test
  public void testGetProjectsByOwnerNullOwnerId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsByOwner(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsByOwner method with an empty owner id.
   */
  @Test
  public void testGetProjectsByOwnerEmptyOwnerId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsByOwner("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsByOwner method when the owner is not found.
   */
  @Test
  public void testGetProjectsByOwnerOwnerNotFound() {
    // Arrange
    when(userRepository.existsById(owner.getUserId())).thenReturn(false);

    // Act
    ResponseEntity<?> response = projectService.getProjectsByOwner(owner.getUserId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsByOwner method when no projects are found.
   */
  @Test
  public void testGetProjectsByOwnerNoProjectsFound() {
    // Arrange
    when(userRepository.existsById(owner.getUserId())).thenReturn(true);
    when(projectRepository.findProjectsByOwnerId(owner.getUserId())).thenReturn(new ArrayList<>());

    // Act
    ResponseEntity<?> response = projectService.getProjectsByOwner(owner.getUserId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "No projects found for the owner.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getProjectsTags method.
   */
  @Test
  public void testGetProjectsTagsSuccess() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(artifactRepository.findArtifactByArtifactId(artifact1.getArtifactId()))
        .thenReturn(artifact1);
    when(artifactRepository.findArtifactByArtifactId(artifact2.getArtifactId()))
        .thenReturn(artifact2);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTags(project.getProjectId());

    // Assert
    List<String> tags = new ArrayList<>();
    tags.addAll(artifact1.getTags());
    tags.addAll(artifact2.getTags());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags found successfully.");
    responseBody.put("tags", tags);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTags method with a null project id.
   */
  @Test
  public void testGetProjectsTagsNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsTags(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTags method with an empty project id.
   */
  @Test
  public void testGetProjectsTagsEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsTags("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTags method when the project is not found.
   */
  @Test
  public void testGetProjectsTagsProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTags(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTags method whe the LoggedUser is not the owner.
   */
  @Test
  public void testGetProjectsTagsNotOwner() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    project.setOwnerId("otherUserId");

    // Act
    ResponseEntity<?> response = projectService.getProjectsTags(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Only the project owner can see the tags!");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTags method when one of the artifactId list is null.
   */
  @Test
  public void testGetProjectsTagsNullArtifacts() {
    // Arrange
    project.setArtifactIds(null);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTags(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "No artifacts found for the project.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTags method when one of the artifactId List is Empty.
   */
  @Test
  public void testGetProjectsTagsNoArtifacts() {
    // Arrange
    List<String> emptyList = new ArrayList<>();
    project.setArtifactIds(emptyList);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTags(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "No artifacts found for the project.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTags method when an artifact is not found.
   */
  @Test
  public void testGetProjectsTagsArtifactNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(artifactRepository.findArtifactByArtifactId(artifact1.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTags(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact with ID " + artifact1.getArtifactId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the addUsersToProject method.
   *
   * @throws Exception if something goes wrong with the method
   */
  @Test
  public void testAddUsersToProjectSuccess() throws Exception {
    // Arrange
    User userToAdd = new User("userId", "addme@example.com", "ryidawkmn", "Marco", "Rossi");
    userToAdd.setUserId("6798e2740b66b85362a8ba29");
    List<String> userEmails = new ArrayList<>();
    userEmails.add(otherUser.getEmail());
    userEmails.add(userToAdd.getEmail());

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);
    when(userRepository.findByEmail(userToAdd.getEmail())).thenReturn(userToAdd);
    when(userRepository.findByUserId(otherUser.getUserId())).thenReturn(otherUser);
    when(userRepository.findByUserId(userToAdd.getUserId())).thenReturn(userToAdd);
    when(userRepository.save(otherUser)).thenReturn(otherUser);
    when(userRepository.save(userToAdd)).thenReturn(userToAdd);
    when(projectRepository.save(project)).thenReturn(project);

    try (MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class, (mock,
        context) -> doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {
      // Act
      projectService.addUsersToProject(project.getProjectId(), userEmails);

      // Assert
      assertTrue(project.getUserIds().contains(otherUser.getUserId()));
      assertTrue(project.getUserIds().contains(userToAdd.getUserId()));
      assertTrue(otherUser.getProjectIds().contains(project.getProjectId()));
      assertTrue(userToAdd.getProjectIds().contains(project.getProjectId()));
      verify(userRepository, times(1)).save(otherUser);
      verify(userRepository, times(1)).save(userToAdd);
      verify(projectRepository, times(2)).save(project);
    }
  }

  /**
   * Tests an execution of the addUsersToProject method when the projectId is not found.
   */
  @Test
  public void testAddUsersToProjectProjectNotFound() {
    // Arrange
    List<String> userEmails = new ArrayList<>();
    userEmails.add(otherUser.getEmail());

    // Act & Assert
    ProjectValidationException exception = assertThrows(ProjectValidationException.class,
        () -> projectService.addUsersToProject(project.getProjectId(), userEmails));
    assertEquals("Project with ID " + project.getProjectId() + " not found.",
        exception.getMessage());
  }

  /**
   * Tests an execution of the addUsersToProject method when a user other than the owner tries to
   * add users to the project.
   */
  @Test
  public void testAddUsersToProjectNotOwner() {
    // Arrange
    project.setOwnerId("otherUserId");
    List<String> userEmails = new ArrayList<>();
    userEmails.add(otherUser.getEmail());
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act & Assert
    ProjectValidationException exception = assertThrows(ProjectValidationException.class,
        () -> projectService.addUsersToProject(project.getProjectId(), userEmails));
    assertEquals("Only the owner can add users to the project.", exception.getMessage());
  }

  /**
   * Tests an execution of the addUsersToProject method when the checkEmails method trows an
   * exception.
   */
  @Test
  public void testAddUsersToProjectCheckEmailsException() {
    // Arrange
    List<String> userEmails = new ArrayList<>();
    userEmails.add(otherUser.getEmail());
    userEmails.add(null);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);

    // Act & Assert
    ProjectValidationException exception = assertThrows(ProjectValidationException.class,
        () -> projectService.addUsersToProject(project.getProjectId(), userEmails));
    assertEquals("There is an empty email in the list. Please remove it.", exception.getMessage());
  }

  /**
   * Tests an execution of the addUsersToProject method when one the users to add is already in the
   * project.
   */
  @Test
  public void testAddUsersToProjectUserAlreadyInProject() throws Exception {
    List<String> userEmails = new ArrayList<>();
    userEmails.add(user1.getEmail());
    userEmails.add(otherUser.getEmail());

    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(otherUser.getUserId())).thenReturn(otherUser);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.save(otherUser)).thenReturn(otherUser);
    when(projectRepository.save(project)).thenReturn(project);

    try (MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class, (mock,
        context) -> doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {
      // Act
      projectService.addUsersToProject(project.getProjectId(), userEmails);

      // Assert
      assertTrue(project.getUserIds().contains(otherUser.getUserId()));
      assertTrue(otherUser.getProjectIds().contains(project.getProjectId()));
      verify(userRepository, times(1)).save(otherUser);
      verify(projectRepository, times(1)).save(project);
    }
  }

  /**
   * Tests a successful execution of the getProjectArtifacts method.
   */
  @Test
  public void testGetProjectsArtifactsSuccess() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(artifactRepository.findArtifactByArtifactId(artifact1.getArtifactId()))
        .thenReturn(artifact1);
    when(artifactRepository.findArtifactByArtifactId(artifact2.getArtifactId()))
        .thenReturn(artifact2);

    // Act
    ResponseEntity<?> response = projectService.getProjectsArtifacts(project.getProjectId());

    // Assert
    List<Artifact> artifacts = new ArrayList<>();
    artifacts.add(artifact1);
    artifacts.add(artifact2);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifacts found successfully.");
    responseBody.put("artifacts", artifacts);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsArtifacts method with a null project id.
   */
  @Test
  public void testGetProjectsArtifactsNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsArtifacts(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsArtifacts method with an empty project id.
   */
  @Test
  public void testGetProjectsArtifactsEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsArtifacts("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsArtifacts method when the project is not found.
   */
  @Test
  public void testGetProjectsArtifactsProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.getProjectsArtifacts(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsArtifacts method when the project has no artifacts.
   */
  @Test
  public void testGetProjectsArtifactsNoArtifacts() {
    // Arrange
    List<String> emptyList = new ArrayList<>();
    project.setArtifactIds(emptyList);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.getProjectsArtifacts(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "No artifacts found for the project.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsArtifacts method when an artifact is not found.
   */
  @Test
  public void testGetProjectsArtifactsArtifactNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(artifactRepository.findArtifactByArtifactId(artifact1.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.getProjectsArtifacts(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact with ID " + artifact1.getArtifactId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getProjectsTeams method.
   */
  @Test
  public void testGetProjectsTeamsSuccess() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTeams(project.getProjectId());

    // Assert
    List<WholeTeamDto> teams = new ArrayList<>();
    teams.add(TeamMapper.toWholeTeamDto(team));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Teams found successfully.");
    responseBody.put("teams", teams);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTeams method with a null project id.
   */
  @Test
  public void testGetProjectsTeamsNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsTeams(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTeams method with an empty project id.
   */
  @Test
  public void testGetProjectsTeamsEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsTeams("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTeams method when the project is not found.
   */
  @Test
  public void testGetProjectsTeamsProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTeams(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsTeams method when a team is not found.
   */
  @Test
  public void testGetProjectsTeamsTeamNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.getProjectsTeams(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team with ID " + team.getTeamId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the closeProject method.
   */
  @Test
  public void testCloseProjectSuccess() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.closeProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project closed successfully.");
    assertEquals(responseBody, response.getBody());
    assertEquals(ProjectStatus.CLOSED, project.getProjectStatus());
    verify(projectRepository, times(1)).save(project);
  }

  /**
   * Tests an execution of the closeProject method with a null project id.
   */
  @Test
  public void testCloseProjectNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.closeProject(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the closeProject method with an empty project id.
   */
  @Test
  public void testCloseProjectEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.closeProject("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the closeProject method when the project is not found.
   */
  @Test
  public void testCloseProjectProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.closeProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the closeProject method when the project is already closed.
   */
  @Test
  public void testCloseProjectProjectAlreadyClosed() {
    // Arrange
    project.setProjectStatus(ProjectStatus.CLOSED);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.closeProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project is already closed.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the closeProject method when a users other than the owner tries to close
   * the project.
   */
  @Test
  public void testCloseProjectNotOwner() {
    // Arrange
    project.setOwnerId("otherUserId");
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.closeProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Only the project owner can close the project.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * . Tests a successful execution of the getProjectById method.
   */
  @Test
  public void testGetProjectByIdSuccess() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.getProjectById(project.getProjectId());

    // Assert
    WholeProjectDto wholeProjectDto = project.toResponseProjectDto();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(wholeProjectDto, response.getBody());
  }

  /**
   * Tests an execution of the getProjectById method with a null project id.
   */
  @Test
  public void testGetProjectByIdNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectById(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectById method with an empty project id.
   */
  @Test
  public void testGetProjectByIdEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.getProjectById("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getProjectById method when the project is not found.
   */
  @Test
  public void testGetProjectByIdProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.getProjectById(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getProjectsByIds method.
   */
  @Test
  public void testGetProjectsByIdsSuccess() {
    // Arrange
    Project anotherProject = new Project("projectName", "projectDescription",
        Instant.now().toEpochMilli(), Instant.parse("2025-12-31T23:59:59Z").toEpochMilli(),
        "6998e2740b87d85362a8ba58", new ArrayList<>());
    anotherProject.setProjectId("6998e2630b87d85362b8ca58");
    project.setUserIds(new ArrayList<>(Arrays.asList("6998e2740b87d85362a8ba58",
        "6798e2740b80b85362a8ba90", otherUser.getUserId())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.findProjectByProjectId(anotherProject.getProjectId()))
        .thenReturn(anotherProject);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    List<String> projectIds =
        new ArrayList<>(Arrays.asList(project.getProjectId(), anotherProject.getProjectId()));
    ResponseEntity<?> response = projectService.getProjectsByIds(projectIds);

    // Assert
    List<ProjectInfoDto> projects = new ArrayList<>();
    projects.add(project.toProjectInfoDto());
    projects.add(anotherProject.toProjectInfoDto());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(projects, response.getBody());
  }

  /**
   * Tests an execution of the getProjectsByIds method with a null project id list.
   */
  @Test
  public void testGetProjectsByIdsNullProjectIds() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsByIds(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Project IDs cannot be null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getProjectsByIds method with an empty project id list.
   */
  @Test
  public void testGetProjectsByIdsEmptyProjectIds() {
    // Act
    ResponseEntity<?> response = projectService.getProjectsByIds(new ArrayList<>());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Project IDs cannot be null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getProjectsByIds method when a project is not found.
   */
  @Test
  public void testGetProjectsByIdsProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    List<String> projectIds =
        new ArrayList<>(Arrays.asList(project.getProjectId(), "6998e2630b87d85462b8ca58"));
    ResponseEntity<?> response = projectService.getProjectsByIds(projectIds);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Project with ID 6998e2630b87d85462b8ca58 not found.", response.getBody());
  }

  /**
   * Tests the deleteProject method
   */
  @Test
  public void testDeleteProject() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.save(owner)).thenReturn(owner);
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.save(user2)).thenReturn(user2);
    when(teamService.deleteTeam(team.getTeamId())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(artifactService.deleteArtifact(artifact1.getArtifactId()))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(artifactService.deleteArtifact(artifact2.getArtifactId()))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    // Act
    ResponseEntity<?> response = projectService.deleteProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project deleted successfully.");
    assertEquals(responseBody, response.getBody());
//    verify(projectRepository, times(1)).delete(project);
//    verify(artifactService, times(1)).
//        deleteArtifact(artifact1.getArtifactId());
//    verify(artifactRepository, times(1)).
//        delete(artifact1);
//    verify(artifactService, times(1)).
//        deleteArtifact(artifact2.getArtifactId());
//    verify(artifactRepository, times(1)).
//        delete(artifact2);
//    verify(teamService, times(1)).
//        deleteTeam(team.getTeamId());
//    verify(teamRepository, times(1)).
    //delete(team);
    verify(userRepository, times(2)).save(owner);
    verify(userRepository, times(1)).save(user1);
    verify(userRepository, times(1)).save(user2);
    assertTrue(owner.getProjectIds().isEmpty());
    assertTrue(user1.getProjectIds().isEmpty());
    assertTrue(user2.getProjectIds().isEmpty());
  }

  /**
   * Tests an execution of the deleteProject method with a null project id.
   */
  @Test
  public void testDeleteProjectNullProjectId() {
    // Act
    ResponseEntity<?> response = projectService.deleteProject(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteProject method with an empty project id.
   */
  @Test
  public void testDeleteProjectEmptyProjectId() {
    // Act
    ResponseEntity<?> response = projectService.deleteProject("");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteProject method when the project is not found.
   */
  @Test
  public void testDeleteProjectProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = projectService.deleteProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteProject method when a user other than owner
   * tries to delete the project
   */
  @Test
  public void testDeleteProjectNotOwner() {
    // Arrange
    project.setOwnerId("otherUserId");
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = projectService.deleteProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Only the owner can delete the project!");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteProject method when the project deletion fails
   */
  @Test
  public void testDeleteProjectProjectDeletionFails() {
    // Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.save(owner)).thenReturn(owner);
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.save(user2)).thenReturn(user2);
    when(teamService.deleteTeam(team.getTeamId())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(artifactService.deleteArtifact(artifact1.getArtifactId()))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(artifactService.deleteArtifact(artifact2.getArtifactId()))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);

    // Act
    ResponseEntity<?> response = projectService.deleteProject(project.getProjectId());

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not deleted.");
    assertEquals(responseBody, response.getBody());
  }


}
