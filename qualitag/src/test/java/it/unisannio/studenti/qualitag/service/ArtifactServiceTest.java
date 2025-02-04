package it.unisannio.studenti.qualitag.service;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertEquals;


import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.websocket.MessageHandler.Whole;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

/**
 * Test class for the ArtifactService.
 */
class ArtifactServiceTest {

  @Mock
  private ArtifactRepository artifactRepository;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private TeamRepository teamRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private Validator validator;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private MultipartFile file;

  @InjectMocks
  private ArtifactService artifactService;

  private ArtifactCreateDto artifactCreateDto;
  private WholeArtifactDto wholeArtifactDto;
  private Artifact artifact;
  private Project project;
  private User user1, user2, owner, otherUser;
  private Team team;
  private Tag tag1, tag2;

  private Path mockFilePath;


  /**
   * Set up the test environment.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    //Initialize the objects
    tag1 = new Tag("TAG1", "6798e2740b80b85362a8ba90", "#fff8de");
    tag1.setTagId("6744ba6c60e0564864250e89");
    tag2 = new Tag("TAG2", "6798e2740b80b85362a8ba90", "#295f98");
    tag2.setTagId("6755b79afc22f97c06a34275");

    user1 = new User("user1", "user1@example.com",
        "password1", "Jane", "Doe");
    user1.setUserId("user1Id");
    user1.setTagIds(new ArrayList<>(Arrays.asList(tag1.getTagId()
        , tag2.getTagId())));
    user2 = new User("user2", "user2@example.com",
        "password2", "John", "Doe");
    user2.setUserId("user2Id");
    owner = new User("owner", "owner@example.com",
        "password3", "Alice", "Smith");
    owner.setUserId("ownerId");
    otherUser = new User("otherUser", "other@example.com", "password",
        "Bob", "Johnson");

    mockFilePath = Paths.get("/mock/path/to/file.txt");

    artifact = new Artifact("artifactName",
        "projectId", "teamId", mockFilePath.toString());
    artifact.setArtifactId("6754705c8d6446369ca02b62");
    artifact.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId()
        , tag2.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact.getArtifactId())));
    tag2.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact.getArtifactId())));

    project = new Project("projectName", "projectDescription",
        0L, 0L, "ownerId", new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("ownerId", "user1Id", "user2Id")));
    user1.setProjectIds(new ArrayList<>(List.of("projectId")));
    owner.setProjectIds(new ArrayList<>(List.of("projectId")));
    project.setTeamIds(new ArrayList<>(List.of("teamId")));

    team = new Team("teamName", "projectId",
        123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("user1Id", "user2Id")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(List.of("teamId")));
    user2.setTeamIds(new ArrayList<>(List.of("teamId")));

    //Initialize the DTO
    artifactCreateDto = new ArtifactCreateDto(artifact.getArtifactName(),
        artifact.getDescription(), project.getProjectId(), file);
    wholeArtifactDto =
        new WholeArtifactDto(artifact.getArtifactId(),
            artifact.getArtifactName(), artifact.getDescription(), artifact.getProjectId(),
            artifact.getTeamId(), artifact.getFilePath(), artifact.getTags(),
            artifact.isTaggingOpen());

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
   * Tests a successful execution of the addArtifact method.
   */
  @Test
  public void testAddArtifact_Success() throws IOException {
    //Arrange
    when(projectRepository.findProjectByProjectId(artifactCreateDto.projectId()))
        .thenReturn(project);
    when(userRepository.findByUserId(project.getOwnerId())).thenReturn(owner);
    when(teamRepository.findTeamByTeamId(team.getTeamId())).thenReturn(team);
    when(projectRepository.save(any(Project.class))).thenReturn(project);
    when(userRepository.save(any(User.class))).thenReturn(user1);
    when(userRepository.save(any(User.class))).thenReturn(user2);
    when(userRepository.save(any(User.class))).thenReturn(owner);
    when(artifactRepository.save(any(Artifact.class))).thenReturn(artifact);
    when(tagRepository.save(any(Tag.class))).thenReturn(tag1);
    when(tagRepository.save(any(Tag.class))).thenReturn(tag2);
    when(file.getOriginalFilename()).thenReturn("fileName");
    when(file.getBytes()).thenReturn(new byte[0]);

    //Act
    ResponseEntity<?> response = artifactService.addArtifact(artifactCreateDto);

    System.out.println(response);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact created successfully.");
    assertEquals(responseBody, response.getBody());
    verify(artifactRepository).save(any(Artifact.class));
  }

  /**
   * Tests an execution of the addArtifact method with an invalid dto.
   */
  @Test
  public void testAddArtifact_InvalidDto() {
    //Arrange
    ArtifactCreateDto invalidDto = new ArtifactCreateDto(null,
        "artifactDescription", project.getProjectId(), file);

    //Act
    ResponseEntity<?> response = artifactService.addArtifact(invalidDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid artifact data.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addArtifact method when the project is not found
   */
  @Test
  public void testAddArtifact_ProjectNotFound() {
    //Arrange
    when(projectRepository.findProjectByProjectId(artifactCreateDto.projectId()))
        .thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.addArtifact(artifactCreateDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addArtifact method when the user is not the project owner
   */
  @Test
  public void testAddArtifact_UserNotOwner() {
    //Arrange
    when(projectRepository.findProjectByProjectId(artifactCreateDto.projectId()))
        .thenReturn(project);
    when(userRepository.findByUserId(project.getOwnerId())).thenReturn(user1);

    //Act
    ResponseEntity<?> response = artifactService.addArtifact(artifactCreateDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not the project owner.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getArtifact method.
   */
  @Test
  public void testGetArtifact_Success(){
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      //Arrange
      when(artifactRepository.findArtifactByArtifactId
          (artifact.getArtifactId())).thenReturn(artifact);
      when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
      when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
      mockedFiles.when(() -> Files.exists(mockFilePath)).thenReturn(true);
      mockedFiles.when(() -> Files.probeContentType(mockFilePath)).thenReturn("text/plain");
      mockedFiles.when(() -> Files.size(mockFilePath)).thenReturn(100L);

      //Act
      ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

      System.out.println(response.getBody());

      //Assert
      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      HttpHeaders headers = response.getHeaders();
      assertTrue(headers.containsKey(HttpHeaders.CONTENT_DISPOSITION));
      assertEquals("attachment; filename=file.txt\"",
          headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
      assertEquals("text/plain", headers.getContentType().toString());
    }
  }

  /**
   * Tests an execution of the getArtifact method when the artifact is not found
   */
  @Test
  public void testGetArtifact_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifact method when the user is not found
   */
  @Test
  public void testGetArtifact_UserNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifact method when the user is not authorized to view
   * the artifact
   */
  @Test
  public void testGetArtifact_UserNotAuthorized() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(otherUser);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    //Act
    ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to view this artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifact method when the file is not found
   */
  @Test
  public void testGetArtifact_FileNotFound() {
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      //Arrange
      when(artifactRepository.findArtifactByArtifactId
          (artifact.getArtifactId())).thenReturn(artifact);
      when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
      when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
      mockedFiles.when(() -> Files.exists(mockFilePath)).thenReturn(false);

      //Act
      ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

      //Assert
      assertNotNull(response);
      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "File not found.");
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests an execution of the getArtifact method when the file type is not found
   */
  @Test
  public void testGetArtifact_IOException(){
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      //Arrange
      when(artifactRepository.findArtifactByArtifactId
          (artifact.getArtifactId())).thenReturn(artifact);
      when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
      when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
      mockedFiles.when(() -> Files.exists(mockFilePath)).thenReturn(true);
      mockedFiles.when(() -> Files.probeContentType(mockFilePath)).thenThrow(new IOException());

      //Act
      ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

      //Assert
      assertNotNull(response);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

  /**
   * Tests a successful execution of the getArtifactMetadata method.
   */
  @Test
  public void testGetArtifactMetadata_Success() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    try (MockedStatic<ArtifactMapper> mockedMapper = mockStatic(ArtifactMapper.class)) {
      mockedMapper.when(() -> ArtifactMapper.toWholeArtifactDto(artifact))
          .thenReturn(wholeArtifactDto);

      //Act
      ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

      //Assert
      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Artifact metadata retrieved successfully.");
      responseBody.put("artifact", wholeArtifactDto);
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests an execution of the getArtifactMetadata method when the artifact is not found
   */
  @Test
  public void testGetArtifactMetadata_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifactMetadata method when the user is not found
   */
  @Test
  public void testGetArtifactMetadata_UserNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifactMetadata method when the user is not authorized
   */
  @Test
  public void testGetArtifactMetadata_UserNotAuthorized() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(otherUser);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    //Act
    ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to view this artifact.");
    assertEquals(responseBody, response.getBody());
  }




}