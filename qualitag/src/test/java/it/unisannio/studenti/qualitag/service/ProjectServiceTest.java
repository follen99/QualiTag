package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
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

  private ProjectCreateDto projectCreateDto;
  private Artifact artifact;
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

    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    artifact.setArtifactId("6754705c8d6446369ca02b62");
    artifact.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId()
        , tag2.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact.getArtifactId())));
    tag2.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact.getArtifactId())));

    project = new Project("projectName", "projectDescription",
        0L, 0L, "6998e2740b87d85362a8ba58",
        new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("6998e2740b87d85362a8ba58",
        "6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    user1.setProjectIds(new ArrayList<>(List.of("projectId")));
    owner.setProjectIds(new ArrayList<>(List.of("projectId")));
    project.setTeamIds(new ArrayList<>(List.of("teamId")));

    team = new Team("teamName", "projectId",
        123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(List.of("teamId")));
    user2.setTeamIds(new ArrayList<>(List.of("teamId")));

    //Initialize the DTO
    projectCreateDto = new ProjectCreateDto("projectName",
        "projectDescription", "2025-12-31T23:59:59Z",
        new ArrayList<>(Arrays.asList("user1@example.com", "user2@example.com")));

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
  public void testCreateProject_Success(){
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
  public void testCreateProject_FailureTeamCreation(){
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
  public void testCreateProject_FailureAddProjectToUser(){
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
}
