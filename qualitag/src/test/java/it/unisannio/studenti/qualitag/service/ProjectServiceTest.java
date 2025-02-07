package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import it.unisannio.studenti.qualitag.dto.project.WholeProjectHeavyDto;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.mapper.TeamMapper;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
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

  private ProjectCreateDto projectCreateDto, projectModifyDto;
  private Artifact artifact1, artifact2;
  private Project project;
  private User user1, user2, owner, otherUser;
  private Team team;
  private Tag tag1, tag2, tag3, tag4;


  /**
   * Sets up the test environment.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    //Initialize the objects
    tag1 = new Tag("TAG1", "6798e2740b80b85362a8ba90", "#fff8de");
    tag1.setTagId("6744ba6c60e0564864250e89");
    tag2 = new Tag("TAG2", "6798e2740b80b85362a8ba90", "#295f98");
    tag2.setTagId("6755b79afc22f97c06a34275");
    tag3 = new Tag("TAG3", "6798e2740b80b85362a8ba90", "#295f98");
    tag3.setTagId("6755b79afc22f97c06a34276");
    tag4 = new Tag("TAG4", "6798e2740b80b85362a8ba90", "#295f98");
    tag4.setTagId("6755b79afc22f97c06a34277");

    user1 = new User("user1", "user1@example.com",
        "password1", "Jane", "Doe");
    user1.setUserId("6798e2740b80b85362a8ba90");
    user1.setTagIds(new ArrayList<>(Arrays.asList(tag1.getTagId()
        , tag2.getTagId())));
    user2 = new User("user2", "user2@example.com",
        "password2", "John", "Doe");
    user2.setUserId("6798e6740b70b85362a8ba91");
    owner = new User("owner", "owner@example.com",
        "password3", "Alice", "Smith");
    owner.setUserId("6998e2740b87d85362a8ba58");
    otherUser = new User("otherUser", "other@example.com", "password",
        "Bob", "Johnson");
    otherUser.setUserId("6798e2740b80b85362a8ba92");

    artifact1 = new Artifact("artifact1Name",
        "projectId", "teamId", "filePath1");
    artifact1.setArtifactId("6754705c8d6446369ca02b62");
    artifact1.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId()
        , tag2.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact1.getArtifactId())));
    tag2.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact1.getArtifactId())));
    artifact2 = new Artifact("artifact2Name",
        "projectId", "teamId", "filePath2");
    artifact2.setArtifactId("6754705c8d6446369ca02b63");
    artifact2.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId(), tag3.getTagId()
        , tag4.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(Arrays.asList(artifact1.getArtifactId(),
        artifact2.getArtifactId())));
    tag3.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact2.getArtifactId())));
    tag4.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact2.getArtifactId())));

    long creationDate = Instant.now().toEpochMilli();
    long deadline = Instant.parse("2025-12-31T23:59:59Z").toEpochMilli();
    project = new Project("projectName", "projectDescription",
        creationDate, deadline, "6998e2740b87d85362a8ba58",
        new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("6998e2740b87d85362a8ba58",
        "6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    user1.setProjectIds(new ArrayList<>(List.of("projectId")));
    owner.setProjectIds(new ArrayList<>(List.of("projectId")));
    project.setTeamIds(new ArrayList<>(List.of("teamId")));
    project.setArtifactIds(new ArrayList<>(Arrays.asList(artifact1.getArtifactId(),
        artifact2.getArtifactId())));

    team = new Team("teamName", "projectId",
        123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(List.of("teamId")));
    user2.setTeamIds(new ArrayList<>(List.of("teamId")));

    //Initialize the DTO
    projectCreateDto = new ProjectCreateDto("projectName",
        "projectDescription", "2025-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), user2.getEmail())));
    projectModifyDto = new ProjectCreateDto("projectNewName",
        "projectNewDescription", "2026-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), otherUser.getEmail())));

    //Initialize authorization details
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
   * Test a successful execution of the createProject method
   */
  @Test
  public void testCreateProject_Success() {
    //Arrange
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamService.addTeam(any(TeamCreateDto.class))).
        thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

    //Mock static method before invoking validateProject
    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class);
        MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class,
            (mock, context) ->
                doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      mockedMapper.when(() -> ProjectMapper.toEntity(any(CompletedProjectCreationDto.class)))
          .thenReturn(project); //Ensure this runs before validateProject

      //Act
      ResponseEntity<?> response = projectService.createProject(projectCreateDto);

      //Assert
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Project created successfully.");
      responseBody.put("projectId", project.getProjectId());
      assertEquals(responseBody, response.getBody());
      verify(projectRepository).save(project);
    }
  }

  /**
   * Test an execution of the createProject method with an invalidDto
   */
  @Test
  public void testCreateProject_InvalidDto() {
    //Arrange
    ProjectCreateDto invalidDto = new ProjectCreateDto("", "",
        "", new ArrayList<>());

    //Act
    ResponseEntity<?> response = projectService.createProject(invalidDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the createProject method with an existing project name
   */
  @Test
  public void testCreateProject_ExistingProjectName() {
    //Arrange
    when(projectRepository.existsByProjectName("projectName")).thenReturn(true);

    //Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project with name 'projectName' already exists.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the createProject method with the deadline in the past
   */
  @Test
  public void testCreateProject_PastDeadline() {
    //Arrange
    ProjectCreateDto pastDeadlineDto = new ProjectCreateDto("projectName",
        "projectDescription", "2020-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    //Act
    ResponseEntity<?> response = projectService.createProject(pastDeadlineDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be before the creation date.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the createProject method with the deadline to far in the future
   */
  @Test
  public void testCreateProject_TooFarDeadline() {
    //Arrange
    ProjectCreateDto futureDeadlineDto = new ProjectCreateDto("projectName",
        "projectDescription", "2035-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

    //Act
    ResponseEntity<?> response = projectService.createProject(futureDeadlineDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be after "
        + ProjectConstants.MAX_PROJECT_DEADLINE_YEARS + " years from now.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner with an empty id
   */
  @Test
  public void testCreateProject_OwnerIdEmpty() {
    //Arrange
    owner.setUserId("");

    //Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner with a null id
   */
  @Test
  public void testCreateProject_OwnerIdNull() {
    //Arrange
    owner.setUserId(null);

    //Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner that does not exist
   */
  @Test
  public void testCreateProject_OwnerNotFound() {
    //Arrange
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + owner.getUserId() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method with an owner that's part of the email list
   */
  @Test
  public void testCreateProject_OwnerInEmailList() {
    //Arrange
    ProjectCreateDto ownerEmailDto = new ProjectCreateDto("projectName",
        "projectDescription", "2025-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com", owner.getEmail())));

    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    //Act
    ResponseEntity<?> response = projectService.createProject(ownerEmailDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner must not be part of the list of users.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when there's an empty email in the list
   */
  @Test
  public void testCreateProject_EmptyEmail() {
    //Arrange
    ProjectCreateDto emptyEmailDto = new ProjectCreateDto("projectName",
        "projectDescription", "2025-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), null)));
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    //Act
    ResponseEntity<?> response = projectService.createProject(emptyEmailDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "There is an empty email in the list. Please remove it.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when there's a repeated email in the list
   */
  @Test
  public void testCreateProject_RepeatedEmail() {
    //Arrange
    ProjectCreateDto repeatedEmailDto = new ProjectCreateDto("projectName",
        "projectDescription", "2025-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), user1.getEmail())));
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    //Act
    ResponseEntity<?> response = projectService.createProject(repeatedEmailDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg",
        "User with email " + user1.getEmail() + " is mentioned more than once.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when a user in the list does not exist
   */
  @Test
  public void testCreateProject_UserNotFound() {
    //Arrange
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.createProject(projectCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with email " + user1.getEmail() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createProject method when the team creation fails
   */
  @Test
  public void testCreateProject_FailureTeamCreation() {
    //Arrange
    //We use a spy so we test later if the deleteProject method was called
    ProjectService projectServiceSpy = Mockito.spy(projectService);
    when(projectRepository.existsByProjectName("projectName")).thenReturn(false);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    //Simulate team creation failure (BAD_REQUEST instead of CREATED)
    when(teamService.addTeam(any(TeamCreateDto.class))).
        thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    //Mock the static method
    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class)) {
      mockedMapper.when(() -> ProjectMapper.toEntity(any(CompletedProjectCreationDto.class)))
          .thenReturn(project);

      //Act
      ResponseEntity<?> response = projectServiceSpy.createProject(projectCreateDto);

      //Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      //Verify that the project was saved initially
      verify(projectRepository).save(any(Project.class));
      //Verify that deleteProject was called to roll back the project
      verify(projectServiceSpy, times(1))
          .deleteProject(project.getProjectId());
    }
  }

  /**
   * Tests an execution of the createProject method when the addProjectToUser method fails
   */
  @Test
  public void testCreateProject_FailureAddProjectToUser() {
    //Arrange
    //Use a spy so we test later if the deleteProject method was called
    ProjectService projectServiceSpy = Mockito.spy(projectService);
    //Set the project ownerId to null, so that the addProjectToUser method fails
    project.setOwnerId("otherUserId");
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamService.addTeam(any(TeamCreateDto.class))).
        thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

    //Mock static method before invoking validateProject
    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class);
        MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class,
            (mock, context) ->
                doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      mockedMapper.when(() -> ProjectMapper.toEntity(any(CompletedProjectCreationDto.class)))
          .thenReturn(project);

      //Act
      ResponseEntity<?> response = projectServiceSpy.createProject(projectCreateDto);

      //Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "User with ID otherUserId not found.");
      assertEquals(responseBody, response.getBody());
      verify(projectRepository).save(project);
      verify(projectServiceSpy, times(1))
          .deleteProject(project.getProjectId());
    }

  }

  /**
   * Tests a successful execution of the updateProject method
   */
  @Test
  public void testUpdateProject_Success() {
    //Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);
    when(userRepository.existsByEmail(otherUser.getEmail())).thenReturn(true);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(otherUser.getUserId())).thenReturn(otherUser);
    when(teamRepository.findTeamsByProjectId(project.getProjectId())).
        thenReturn(new ArrayList<>(Collections.singletonList(team)));
    when(teamRepository.save(team)).thenReturn(team);
    when(userRepository.save(user1)).thenReturn(user1);
    when(userRepository.save(user2)).thenReturn(user2);
    when(userRepository.save(otherUser)).thenReturn(otherUser);
    when(projectRepository.save(project)).thenReturn(project);

    try (MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class,
        (mock, context) ->
            doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      //Act
      ResponseEntity<?> response = projectService.
          updateProject(projectModifyDto, project.getProjectId());

      System.out.println(response.getBody());

      //Assert
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
   * Tests an execution of the updateProject method with a null project id
   */
  @Test
  public void testUpdateProject_NullProjectId() {
    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto, null);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with an empty project id
   */
  @Test
  public void testUpdateProject_EmptyProjectId() {
    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto, "");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with a project id that does not exist
   */
  @Test
  public void testUpdateProject_ProjectIdNotFound() {
    //Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when a user that is not the owner tries to
   * update the project
   */
  @Test
  public void testUpdateProject_NotOwner() {
    //Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    project.setOwnerId("otherUserId");

    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Only the owner can modify the project!");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with an invalidDto
   */
  @Test
  public void testUpdateProject_InvalidDto() {
    //Arrange
    ProjectCreateDto invalidDto = new ProjectCreateDto("", "",
        "", new ArrayList<>());
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    //Act
    ResponseEntity<?> response = projectService.updateProject(invalidDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with an existing project name
   */
  @Test
  public void testUpdateProject_ExistingProjectName() {
    //Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(projectRepository.existsByProjectName("projectNewName")).thenReturn(true);

    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto,
        project.getProjectId());

    System.out.println(response.getBody());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project with name projectNewName already exists.");
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests an execution of the updateProject method with the deadline in the past
   */
  @Test
  public void testUpdateProject_PastDeadline() {
    //Arrange
    ProjectCreateDto pastDeadlineDto = new ProjectCreateDto("projectNewName",
        "projectNewDescription", "2020-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), owner.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    //Act
    ResponseEntity<?> response = projectService.updateProject(pastDeadlineDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be before the creation date.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method with the deadline too far in the future
   */
  @Test
  public void testUpdateProject_TooFarDeadline() {
    //Arrange
    ProjectCreateDto futureDeadlineDto = new ProjectCreateDto("projectNewName",
        "projectNewDescription", "2035-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), otherUser.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);

    //Act
    ResponseEntity<?> response = projectService.updateProject(futureDeadlineDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Deadline date cannot be after "
        + ProjectConstants.MAX_PROJECT_DEADLINE_YEARS + " years from now.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when the owner is part of the user list
   */
  @Test
  public void testUpdateProject_OwnerInUserList() {
    //Arrange
    ProjectCreateDto ownerInUserListDto = new ProjectCreateDto("projectNewName",
        "projectNewDescription", "2026-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), owner.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    //Act
    ResponseEntity<?> response = projectService.updateProject(ownerInUserListDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner must not be part of the list of users.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when there's an empty email in the list
   */
  @Test
  public void testUpdateProject_EmptyEmail() {
    //Arrange
    ProjectCreateDto emptyEmailDto = new ProjectCreateDto("projectNewName",
        "projectNewDescription", "2026-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), null)));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    //Act
    ResponseEntity<?> response = projectService.updateProject(emptyEmailDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "There is an empty email in the list. Please remove it.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when there's a repeated email in the list
   */
  @Test
  public void testUpdateProject_RepeatedEmail() {
    //Arrange
    ProjectCreateDto repeatedEmailDto = new ProjectCreateDto("projectNewName",
        "projectNewDescription", "2026-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList(user1.getEmail(), user1.getEmail())));
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

    //Act
    ResponseEntity<?> response = projectService.updateProject(repeatedEmailDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg",
        "User with email " + user1.getEmail() + " is mentioned more than once.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when an email in the list is not found
   */
  @Test
  public void testUpdateProject_UserNotFound() {
    //Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto,
        project.getProjectId());

    //Assert
    List<String> emailsNotFound = new ArrayList<>(Arrays.asList(user1.getEmail(),
        otherUser.getEmail()));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "The following emails are not registered: " + emailsNotFound);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method one emails in the list is not found
   */
  @Test
  public void testUpdateProject_OneUserNotFound() {
    //Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.existsByEmail(otherUser.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with email " + otherUser.getEmail() + " does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateProject method when the team update fails
   *
   * @throws NullPointerException since the team is not found
   */
  @Test
  public void testUpdateProject_testUpdateFail() throws NullPointerException {
    //Arrange
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
    when(userRepository.existsByEmail(otherUser.getEmail())).thenReturn(true);
    when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);
    when(teamRepository.findTeamsByProjectId(project.getProjectId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.updateProject(projectModifyDto,
        project.getProjectId());

    //Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg",
        "Cannot invoke \"java.util.List.iterator()\" because \"teams\" is null");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getHumanReadableProjectStatus method
   */
  @Test
  public void testGetHumanReadableProjectStatus_Success() {
    //Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId
        (artifact1.getArtifactId())).thenReturn(artifact1);
    when(artifactRepository.findArtifactByArtifactId
        (artifact2.getArtifactId())).thenReturn(artifact2);

    //Create the expected WholeProjectHeavyDto
    List<UserShortResponseDto> shortResponseUserDto = new ArrayList<>();
    shortResponseUserDto.add(UserMapper.toUserShortResponseDto(owner));
    shortResponseUserDto.add(UserMapper.toUserShortResponseDto(user1));
    shortResponseUserDto.add(UserMapper.toUserShortResponseDto(user2));

    List<WholeTeamDto> wholeTeamDto = new ArrayList<>();
    wholeTeamDto.add(TeamMapper.toWholeTeamDto(team));

    List<WholeArtifactDto> wholeArtifactDto = new ArrayList<>();
    wholeArtifactDto.add(ArtifactMapper.toWholeArtifactDto(artifact1));
    wholeArtifactDto.add(ArtifactMapper.toWholeArtifactDto(artifact2));

    UserShortResponseDto ownerDto = UserMapper.toUserShortResponseDto(owner);

    WholeProjectHeavyDto expectedWholeProjectHeavyDto =
        new WholeProjectHeavyDto(project.getProjectId(), project.getProjectName(),
            project.getProjectDescription(), project.getProjectCreationDate(),
            project.getProjectDeadline(), ownerDto, project.getProjectStatus().name(),
            shortResponseUserDto, wholeArtifactDto, wholeTeamDto);

    //Act
    ResponseEntity<?> response = projectService.
        getHumanReadableProjectStatus(project.getProjectId());

    System.out.println(response.getBody());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedWholeProjectHeavyDto, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method with a null project id
   */
  @Test
  public void testGetHumanReadableProjectStatus_NullProjectId() {
    //Act
    ResponseEntity<?> response = projectService.getHumanReadableProjectStatus(null);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method with an empty project id
   */
  @Test
  public void testGetHumanReadableProjectStatus_EmptyProjectId() {
    //Act
    ResponseEntity<?> response = projectService.getHumanReadableProjectStatus("");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project ID cannot be null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method with a project id
   * that does not exist
   */
  @Test
  public void testGetHumanReadableProjectStatus_ProjectIdNotFound() {
    //Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(false);

    //Act
    ResponseEntity<?> response = projectService.
        getHumanReadableProjectStatus(project.getProjectId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when the project is not found
   */
  @Test
  public void testGetHumanReadableProjectStatus_ProjectNotFound() {
    //Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.
        getHumanReadableProjectStatus(project.getProjectId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when a user is not found
   */
  @Test
  public void testGetHumanReadableProjectStatus_UserNotFound() {
    //Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);

    //Act
    ResponseEntity<?> response = projectService.
        getHumanReadableProjectStatus(project.getProjectId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User with ID " + user1.getUserId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when a team is not found
   */
  @Test
  public void testGetHumanReadableProjectStatus_TeamNotFound() {
    //Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.
        getHumanReadableProjectStatus(project.getProjectId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team with ID " + team.getTeamId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when an artifact is not found
   */
  @Test
  public void testGetHumanReadableProjectStatus_ArtifactNotFound() {
    //Arrange
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId
        (artifact1.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = projectService.
        getHumanReadableProjectStatus(project.getProjectId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact with ID " + artifact1.getArtifactId() + " not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getHumanReadableProjectStatus method when
   * the project owner is not found
   */
  @Test
  public void testGetHumanReadableProjectStatus_OwnerNotFound() {
    //Arrange
    project.setOwnerId(otherUser.getUserId());
    when(projectRepository.existsById(project.getProjectId())).thenReturn(true);
    when(projectRepository.findProjectByProjectId(project.getProjectId())).thenReturn(project);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(artifactRepository.findArtifactByArtifactId
        (artifact1.getArtifactId())).thenReturn(artifact1);
    when(artifactRepository.findArtifactByArtifactId
        (artifact2.getArtifactId())).thenReturn(artifact2);

    //Act
    ResponseEntity<?> response = projectService.
        getHumanReadableProjectStatus(project.getProjectId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Owner not found.");
    assertEquals(responseBody, response.getBody());
  }



}
