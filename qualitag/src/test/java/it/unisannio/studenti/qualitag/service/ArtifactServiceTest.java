package it.unisannio.studenti.qualitag.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.dto.artifact.AddTagsToArtifactDto;
import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagResponseDto;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.mapper.TagMapper;
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
import jakarta.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

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
  private PythonClientService pythonClientService;
  @Mock
  private TagService tagService;
  @Mock
  private MultipartFile file;

  @InjectMocks
  private ArtifactService artifactService;

  private ArtifactCreateDto artifactCreateDto;
  private WholeArtifactDto wholeArtifactDto;
  private AddTagsToArtifactDto addTagsToArtifactDto;

  private Artifact artifact;

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

  private Path mockFilePath;


  /**
   * Set up the test environment.
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

    mockFilePath = Paths.get("/mock/path/to/file.txt");

    artifact = new Artifact("artifactName", "projectId", "teamId", mockFilePath.toString());
    artifact.setArtifactId("6754705c8d6446369ca02b62");
    artifact.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId(), tag2.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact.getArtifactId())));
    tag2.setArtifactIds(new ArrayList<>(Collections.singletonList(artifact.getArtifactId())));

    long creationDate = Instant.now().toEpochMilli();
    long deadline = Instant.parse("2025-12-31T23:59:59Z").toEpochMilli();
    project = new Project("projectName", "projectDescription", creationDate, deadline,
        owner.getUserId(), new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("6998e2740b87d85362a8ba58",
        "6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    user1.setProjectIds(new ArrayList<>(List.of("projectId")));
    owner.setProjectIds(new ArrayList<>(List.of("projectId")));
    project.setTeamIds(new ArrayList<>(List.of("teamId")));

    team = new Team("teamName", "projectId", 123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("6798e2740b80b85362a8ba90", "6798e6740b70b85362a8ba91")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(List.of("teamId")));
    user2.setTeamIds(new ArrayList<>(List.of("teamId")));

    // Initialize the DTO
    artifactCreateDto = new ArtifactCreateDto(artifact.getArtifactName(), artifact.getDescription(),
        project.getProjectId(), null, file);
    wholeArtifactDto = new WholeArtifactDto(artifact.getArtifactId(), artifact.getArtifactName(),
        artifact.getDescription(), artifact.getProjectId(), artifact.getTeamId(),
        artifact.getFilePath(), artifact.getTags(), artifact.isTaggingOpen());
    addTagsToArtifactDto =
        new AddTagsToArtifactDto(new ArrayList<>(Arrays.asList(tag3.getTagId(), tag4.getTagId())));

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
   * Tests a successful execution of the addArtifact method.
   */
  @Test
  public void testAddArtifactSuccess() throws IOException {
    // Arrange
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

    // Act
    ResponseEntity<?> response = artifactService.addArtifact(artifactCreateDto);

    // Assert
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
  public void testAddArtifactInvalidDto() {
    // Arrange
    ArtifactCreateDto invalidDto =
        new ArtifactCreateDto(null, "artifactDescription", project.getProjectId(), null, file);

    // Act
    ResponseEntity<?> response = artifactService.addArtifact(invalidDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid artifact data.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addArtifact method when the project is not found.
   */
  @Test
  public void testAddArtifactProjectNotFound() {
    // Arrange
    when(projectRepository.findProjectByProjectId(artifactCreateDto.projectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.addArtifact(artifactCreateDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addArtifact method when the user is not the project owner.
   */
  @Test
  public void testAddArtifactUserNotOwner() {
    // Arrange
    when(projectRepository.findProjectByProjectId(artifactCreateDto.projectId()))
        .thenReturn(project);
    when(userRepository.findByUserId(project.getOwnerId())).thenReturn(user1);

    // Act
    ResponseEntity<?> response = artifactService.addArtifact(artifactCreateDto);

    // Assert
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
  public void testGetArtifactSuccess() {
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      // Arrange
      when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
          .thenReturn(artifact);
      when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
      when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
      mockedFiles.when(() -> Files.exists(mockFilePath)).thenReturn(true);
      mockedFiles.when(() -> Files.probeContentType(mockFilePath)).thenReturn("text/plain");
      mockedFiles.when(() -> Files.size(mockFilePath)).thenReturn(100L);

      // Act
      ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

      // Assert
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
   * Tests an execution of the getArtifact method when the artifact is not found.
   */
  @Test
  public void testGetArtifactArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifact method when the user is not found.
   */
  @Test
  public void testGetArtifactUserNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifact method when the user is not authorized to view the
   * artifact.
   */
  @Test
  public void testGetArtifactUserNotAuthorized() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(otherUser);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to view this artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifact method when the file is not found.
   */
  @Test
  public void testGetArtifactFileNotFound() {
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      // Arrange
      when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
          .thenReturn(artifact);
      when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
      when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
      mockedFiles.when(() -> Files.exists(mockFilePath)).thenReturn(false);

      // Act
      ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

      // Assert
      assertNotNull(response);
      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "File not found.");
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests an execution of the getArtifact method when the file type is not found.
   */
  @Test
  public void testGetArtifactIoException() {
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      // Arrange
      when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
          .thenReturn(artifact);
      when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
      when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
      mockedFiles.when(() -> Files.exists(mockFilePath)).thenReturn(true);
      mockedFiles.when(() -> Files.probeContentType(mockFilePath)).thenThrow(new IOException());

      // Act
      ResponseEntity<?> response = artifactService.getArtifact(artifact.getArtifactId());

      // Assert
      assertNotNull(response);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

  /**
   * Tests a successful execution of the getArtifactMetadata method.
   */
  @Test
  public void testGetArtifactMetadataSuccess() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    try (MockedStatic<ArtifactMapper> mockedMapper = mockStatic(ArtifactMapper.class)) {
      mockedMapper.when(() -> ArtifactMapper.toWholeArtifactDto(artifact))
          .thenReturn(wholeArtifactDto);

      // Act
      ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

      // Assert
      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Artifact metadata retrieved successfully.");
      responseBody.put("artifact", wholeArtifactDto);
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests an execution of the getArtifactMetadata method when the artifact is not found.
   */
  @Test
  public void testGetArtifactMetadataArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifactMetadata method when the user is not found.
   */
  @Test
  public void testGetArtifactMetadataUserNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getArtifactMetadata method when the user is not authorized.
   */
  @Test
  public void testGetArtifactMetadataUserNotAuthorized() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(otherUser);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = artifactService.getArtifactMetadata(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to view this artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the deleteArtifact method.
   */
  @Test
  public void testDeleteArtifactSuccess() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(projectRepository.save(project)).thenReturn(project);
    when(teamRepository.save(team)).thenReturn(team);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(tagRepository.save(tag1)).thenReturn(tag1);
    when(tagRepository.save(tag2)).thenReturn(tag2);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.delete(any(Path.class))).thenAnswer(invocation -> null);

      when(artifactRepository.existsById(artifact.getArtifactId())).thenReturn(false);

      // Act
      ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

      // Assert
      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Artifact deleted successfully.");
      assertEquals(responseBody, response.getBody());

      // Verify that the artifact has been removed from the project, team, and tags
      assertFalse(project.getArtifactIds().contains(artifact.getArtifactId()));
      verify(projectRepository, times(1)).save(project);
      assertFalse(team.getArtifactIds().contains(artifact.getArtifactId()));
      verify(teamRepository, times(1)).save(team);
      assertFalse(tag1.getArtifactIds().contains(artifact.getArtifactId()));
      verify(tagRepository, times(1)).save(tag1);
      assertFalse(tag2.getArtifactIds().contains(artifact.getArtifactId()));
      verify(tagRepository, times(1)).save(tag2);
    }

  }

  /**
   * Tests an execution of the deleteArtifact method when the artifactId provided is empty.
   */
  @Test
  public void testDeleteArtifactEmptyArtifactId() {
    // Arrange
    String emptyArtifactId = "";

    // Act
    ResponseEntity<?> response = artifactService.deleteArtifact(emptyArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the artifactId is null.
   */
  @Test
  public void testDeleteArtifactNullArtifactId() {
    // Arrange
    String nullArtifactId = null;

    // Act
    ResponseEntity<?> response = artifactService.deleteArtifact(nullArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the artifact is not found.
   */
  @Test
  public void testDeleteArtifactArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the project is not found.
   */
  @Test
  public void testDeleteArtifactProjectNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the user is not authorized.
   */
  @Test
  public void testDeleteArtifactUserNotAuthorized() {
    // Arrange
    project.setOwnerId("6798e2740b80b85362a8ba92");
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not the project owner.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the team is not found.
   */
  @Test
  public void testDeleteArtifactTeamNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the file deletion fails.
   */
  @Test
  public void testDeleteArtifactFileDeletionFailure() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(projectRepository.save(project)).thenReturn(project);
    when(teamRepository.save(team)).thenReturn(team);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(tagRepository.save(tag1)).thenReturn(tag1);
    when(tagRepository.save(tag2)).thenReturn(tag2);

    // Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "File deletion failed.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when deletion of the artifact fails.
   */
  @Test
  public void testDeleteArtifactArtifactDeletionFailure() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(projectRepository.save(project)).thenReturn(project);
    when(teamRepository.save(team)).thenReturn(team);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(tagRepository.save(tag1)).thenReturn(tag1);
    when(tagRepository.save(tag2)).thenReturn(tag2);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.delete(any(Path.class))).thenAnswer(invocation -> null);
      when(artifactRepository.existsById(artifact.getArtifactId())).thenReturn(true);

      // Act
      ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

      // Assert
      assertNotNull(response);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Artifact not deleted.");
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests a successful execution of the removeTags method.
   */
  @Test
  public void testRemoveTagsSuccess() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    // when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(teamRepository.save(team)).thenReturn(team);
    when(artifactRepository.save(artifact)).thenReturn(artifact);

    // Act
    ResponseEntity<?> response =
        artifactService.removeTag(artifact.getArtifactId(), tag1.getTagId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag removed successfully.");
    assertEquals(responseBody, response.getBody());

    assertFalse(artifact.getTags().contains(tag1.getTagId()));
    verify(artifactRepository, times(1)).save(artifact);
    assertFalse(tag1.getArtifactIds().contains(artifact.getArtifactId()));
    verify(tagRepository, times(1)).save(tag1);
  }

  /**
   * Tests an execution of the removeTags method when the artifactId provided is empty.
   */
  @Test
  public void testRemoveTagsEmptyArtifactId() {
    // Arrange
    String emptyArtifactId = "";

    // Act
    ResponseEntity<?> response = artifactService.removeTag(emptyArtifactId, tag1.getTagId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the artifactId is null.
   */
  @Test
  public void testRemoveTagsNullArtifactId() {
    // Arrange
    String nullArtifactId = null;

    // Act
    ResponseEntity<?> response = artifactService.removeTag(nullArtifactId, tag1.getTagId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the tagId provided is empty.
   */
  @Test
  public void testRemoveTagsEmptyTagId() {
    // Arrange
    String emptyTagId = "";

    // Act
    ResponseEntity<?> response = artifactService.removeTag(artifact.getArtifactId(), emptyTagId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the tagId is null.
   */
  @Test
  public void testRemoveTagsNullTagId() {
    // Arrange
    String nullTagId = null;

    // Act
    ResponseEntity<?> response = artifactService.removeTag(artifact.getArtifactId(), nullTagId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the artifact is not found.
   */
  @Test
  public void testRemoveTagsArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        artifactService.removeTag(artifact.getArtifactId(), tag1.getTagId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the logged-in user is not the owner of the
   * project.
   */
  @Test
  public void testRemoveTagsUserNotOwner() {
    // Arrange
    project.setOwnerId("6798e2740b80b85362a8ba92");

    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    // Act
    ResponseEntity<?> response =
        artifactService.removeTag(artifact.getArtifactId(), tag1.getTagId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to remove this tag from the artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the tag is not found in the artifact.
   */
  @Test
  public void testRemoveTagsTagNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    // Act
    ResponseEntity<?> response =
        artifactService.removeTag(artifact.getArtifactId(), tag3.getTagId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found in artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the addTags method where the logged-in user is the project
   * owner.
   */
  @Test
  public void testAddTagsSuccessfulOwner() {
    // Assert
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
    when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(artifactRepository.save(artifact)).thenReturn(artifact);
    when(tagRepository.save(tag3)).thenReturn(tag3);
    when(tagRepository.save(tag4)).thenReturn(tag4);

    // Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags added successfully.");
    assertEquals(responseBody, response.getBody());

    assertTrue(artifact.getTags().contains(tag3.getTagId()));
    assertTrue(artifact.getTags().contains(tag4.getTagId()));
    // artifact is saved twice since we add two new tags
    verify(artifactRepository, times(2)).save(artifact);
    assertTrue(tag3.getArtifactIds().contains(artifact.getArtifactId()));
    verify(tagRepository, times(1)).save(tag3);
    assertTrue(tag4.getArtifactIds().contains(artifact.getArtifactId()));
    verify(tagRepository, times(1)).save(tag4);
  }

  // /**
  // * Tests a successful execution of the addTags method where the logged-in user is a team member
  // */
  // @Test
  // public void testAddTagsSuccessfulMember(){
  // //Arrange
  // Authentication authentication = mock(Authentication.class);
  // when(authentication.isAuthenticated()).thenReturn(true);
  // when(authentication.getName()).thenReturn("user1");
  // when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user1));
  // SecurityContext securityContext = mock(SecurityContext.class);
  // when(securityContext.getAuthentication()).thenReturn(authentication);
  // SecurityContextHolder.setContext(securityContext);
  //
  // when(artifactRepository.findArtifactByArtifactId
  // (artifact.getArtifactId())).thenReturn(artifact);
  // when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
  // when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
  // when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
  // when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
  // when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
  //
  // //Act
  // ResponseEntity<?> response =
  // artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);
  //
  // System.out.println(response.getBody());
  //
  // //Assert
  // assertNotNull(response);
  // assertEquals(HttpStatus.OK, response.getStatusCode());
  // Map<String, Object> responseBody = new HashMap<>();
  // responseBody.put("msg", "Tags added successfully.");
  // assertEquals(responseBody, response.getBody());
  //
  // assertTrue(artifact.getTags().contains(tag3.getTagId()));
  // assertTrue(artifact.getTags().contains(tag4.getTagId()));
  // //artifact is saved twice since we add two new tags
  // verify(artifactRepository, times(2)).save(artifact);
  // assertTrue(tag3.getArtifactIds().contains(artifact.getArtifactId()));
  // verify(tagRepository, times(1)).save(tag3);
  // assertTrue(tag4.getArtifactIds().contains(artifact.getArtifactId()));
  // verify(tagRepository, times(1)).save(tag4);
  // }

  /**
   * Tests an execution of the addTags method when the dto provided is invalid.
   */
  @Test
  public void testAddTagsInvalidDto() {
    // Arrange
    AddTagsToArtifactDto invalidDto = new AddTagsToArtifactDto(new ArrayList<>());
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = artifactService.addTags(artifact.getArtifactId(), invalidDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid data.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the artifactId provided is empty.
   */
  @Test
  public void testAddTagsEmptyArtifactId() {
    // Arrange
    String emptyArtifactId = "";

    // Act
    ResponseEntity<?> response = artifactService.addTags(emptyArtifactId, addTagsToArtifactDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the artifactId is null.
   */
  @Test
  public void testAddTagsNullArtifactId() {
    // Arrange
    String nullArtifactId = null;

    // Act
    ResponseEntity<?> response = artifactService.addTags(nullArtifactId, addTagsToArtifactDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the artifact is not found.
   */
  @Test
  public void testAddTagsArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when one of the tagIds provided is empty.
   */
  @Test
  public void testAddTagsEmptyTagId() {
    // Arrange
    AddTagsToArtifactDto invalidDto =
        new AddTagsToArtifactDto(new ArrayList<>(Arrays.asList("", "")));
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = artifactService.addTags(artifact.getArtifactId(), invalidDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when one of the tagIds provided is null.
   */
  @Test
  public void testAddTagsNullTagId() {
    // Arrange
    AddTagsToArtifactDto invalidDto =
        new AddTagsToArtifactDto(new ArrayList<>(Arrays.asList(null, null)));
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = artifactService.addTags(artifact.getArtifactId(), invalidDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the project is not found.
   */
  @Test
  public void testAddTagsProjectNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the tag is not found.
   */
  @Test
  public void testAddTagsTagNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(null);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when one of the tags is already in the artifact.
   */
  @Test
  public void testAddTagsTagAlreadyInArtifact() {
    // Arrange
    AddTagsToArtifactDto addPresentTagsDto =
        new AddTagsToArtifactDto(new ArrayList<>(Arrays.asList(tag1.getTagId(), tag4.getTagId())));
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    // Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addPresentTagsDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag already exists in artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the user is not the owner of th project.
   */
  @Test
  public void testAddTagsUserNotOwner() {
    // Arrange
    project.setOwnerId("6798e2740b80b85362a8ba92");
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
    when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    // Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not the project owner.");
    assertEquals(responseBody, response.getBody());
  }

  // /**
  // * Tests an execution of the addTags method when the user is not authorized to add tags.
  // */
  // @Test
  // public void testAddTagsUserNotAuthorized(){
  // //Arrange
  // Authentication authentication = mock(Authentication.class);
  // when(authentication.isAuthenticated()).thenReturn(true);
  // when(authentication.getName()).thenReturn("user2");
  // when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user2));
  // SecurityContext securityContext = mock(SecurityContext.class);
  // when(securityContext.getAuthentication()).thenReturn(authentication);
  // SecurityContextHolder.setContext(securityContext);
  //
  // when(artifactRepository.findArtifactByArtifactId
  // (artifact.getArtifactId())).thenReturn(artifact);
  // when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
  // when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
  // when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
  // when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
  // when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
  //
  // //Act
  // ResponseEntity<?> response =
  // artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);
  //
  // //Assert
  // assertNotNull(response);
  // assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  // Map<String, Object> responseBody = new HashMap<>();
  // responseBody.put("msg", "User is not authorized to add tags to this artifact.");
  // assertEquals(responseBody, response.getBody());
  // }

  /**
   * Tests a successful execution of the getTagsByUser method (By using its ID).
   */
  @Test
  public void testGetTagsByUserIdSuccess() {
    // Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    // Act
    ResponseEntity<?> response =
        artifactService.getTagsByUser(artifact.getArtifactId(), user1.getUserId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags retrieved successfully.");
    responseBody.put("tags", Arrays.asList(tag1, tag2));
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests a successful execution of the getTagsByUser method (By using its username).
   */
  @Test
  public void testGetTagsByUsernameSuccess() {
    // Arrange
    when(userRepository.findByUsername(user1.getUsername())).thenReturn(user1);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    // Act
    ResponseEntity<?> response =
        artifactService.getTagsByUser(artifact.getArtifactId(), user1.getUsername());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags retrieved successfully.");
    responseBody.put("tags", Arrays.asList(tag1, tag2));
    assertEquals(responseBody, response.getBody());
  }

  // /**
  // * Tests a successful execution of the getTagsByUser (by using his email).
  // */
  // @Test
  // public void testGetTagsByEmailSuccess() {
  // //Arrange
  // when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
  // when(artifactRepository.findArtifactByArtifactId
  // (artifact.getArtifactId())).thenReturn(artifact);
  // when(projectRepository.findProjectByProjectId
  // (artifact.getProjectId())).thenReturn(project);
  // when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
  // when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
  //
  // //Act
  // ResponseEntity<?> response = artifactService.getTagsByUser(artifact.getArtifactId(),
  // user1.getEmail());
  //
  // System.out.println(response.getBody());
  //
  // //Assert
  // assertNotNull(response);
  // assertEquals(HttpStatus.OK, response.getStatusCode());
  // Map<String, Object> responseBody = new HashMap<>();
  // responseBody.put("msg", "Tags retrieved successfully.");
  // responseBody.put("tags", Arrays.asList(tag1, tag2));
  // assertEquals(responseBody, response.getBody());
  // }

  /**
   * Tests an execution of the getTagsByUser method when the user is not found.
   */
  @Test
  public void testGetTagsByUserUserNotFound() {
    // Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        artifactService.getTagsByUser(artifact.getArtifactId(), user1.getUserId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the artifact is not found.
   */
  @Test
  public void testGetTagsByUserArtifactNotFound() {
    // Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response =
        artifactService.getTagsByUser(artifact.getArtifactId(), user1.getUserId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the user is not authorized.
   */
  @Test
  public void testGetTagsByUserUserNotAuthorized() {
    // Arrange
    when(userRepository.findByUserId(otherUser.getUserId())).thenReturn(otherUser);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    // Act
    ResponseEntity<?> response =
        artifactService.getTagsByUser(artifact.getArtifactId(), otherUser.getUserId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to view this artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getAllTags method.
   */
  @Test
  public void testGetAllTagsSuccess() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);

    // Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags retrieved successfully.");
    TagMapper tagMapper = new TagMapper();
    List<TagResponseDto> tags = new ArrayList<>();
    tags.add(tagMapper.getResponseDto(tag1));
    tags.add(tagMapper.getResponseDto(tag2));
    responseBody.put("tags", tags);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the artifactId is empty.
   */
  @Test
  public void testGetAllTagsEmptyArtifactId() {
    // Arrange
    String emptyArtifactId = "";

    // Act
    ResponseEntity<?> response = artifactService.getAllTags(emptyArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the artifactId is null.
   */
  @Test
  public void testGetAllTagsNullArtifactId() {
    // Arrange
    String nullArtifactId = null;

    // Act
    ResponseEntity<?> response = artifactService.getAllTags(nullArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the artifact is not found.
   */
  @Test
  public void testGetAllTagsArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the tag is not found.
   */
  @Test
  public void testGetAllTagsTagNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the user is not found.
   */
  @Test
  public void testGetAllTagsUserNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the stopTagging method.
   */
  @Test
  public void testStopTaggingSuccessful() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tagging stopped successfully.");
    assertEquals(responseBody, response.getBody());
    assertFalse(artifact.isTaggingOpen());
  }

  /**
   * Tests an execution of the stopTagging method when the artifactId provided is empty.
   */
  @Test
  public void testStopTaggingEmptyArtifactId() {
    // Arrange
    String emptyArtifactId = "";

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(emptyArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the stopTagging method when the artifactId is null.
   */
  @Test
  public void testStopTaggingNullArtifactId() {
    // Arrange
    String nullArtifactId = null;

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(nullArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the stopTagging method when the artifact is not found.
   */
  @Test
  public void testStopTaggingArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the startTagging method.
   */
  @Test
  public void testStartTaggingSuccessful() {
    // Arrange
    artifact.setTaggingOpen(false);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);

    // Act
    ResponseEntity<?> response = artifactService.startTagging(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tagging started successfully.");
    assertEquals(responseBody, response.getBody());
    assertTrue(artifact.isTaggingOpen());
  }

  /**
   * Tests an execution of the startTagging method when the artifactId provided is empty.
   */
  @Test
  public void testStartTaggingEmptyArtifactId() {
    // Arrange
    String emptyArtifactId = "";

    // Act
    ResponseEntity<?> response = artifactService.startTagging(emptyArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the startTagging method when the artifactId is null.
   */
  @Test
  public void testStartTaggingNullArtifactId() {
    // Arrange
    String nullArtifactId = null;

    // Act
    ResponseEntity<?> response = artifactService.startTagging(nullArtifactId);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the startTagging method when the artifact is not found.
   */
  @Test
  public void testStartTaggingArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.startTagging(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the processTags method.
   */
  @Test
  public void testProcessTagsSuccess() {
    // Arrange
    ArtifactService artifactServiceSpy = Mockito.spy(artifactService);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(artifactRepository.save(artifact)).thenReturn(artifact);
    when(tagRepository.save(tag1)).thenReturn(tag1);
    when(tagRepository.save(tag2)).thenReturn(tag2);

    //We must use doRetun() for method for spies
    doReturn(ResponseEntity.ok().build()).when(artifactServiceSpy)
        .addTags(eq(artifact.getArtifactId()), any(AddTagsToArtifactDto.class));
    String mockProcessedTagsJson = "{ \"result\": [\"processedTag1\", \"processedTag2\"] }";
    when(pythonClientService.processTags(anyList())).thenReturn(mockProcessedTagsJson);
    when(tagService.createTag(any(TagCreateDto.class)))
        .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("tagId", "newTagId1")))
        .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("tagId", "newTagId2")));
    List<String> tags = new ArrayList<>(Arrays.asList(tag1.getTagId(), tag2.getTagId()));
    tags.add("newTagId1");
    tags.add("newTagId2");
    AddTagsToArtifactDto addTagsToArtifactDto = new AddTagsToArtifactDto(tags);
    doReturn(ResponseEntity.ok().build()).when(artifactServiceSpy).addTags(artifact.getArtifactId(),
        addTagsToArtifactDto);

    // Act
    ResponseEntity<?> response = artifactServiceSpy.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags processed successfully.");
    responseBody.put("processedTags", Arrays.asList("processedTag1", "processedTag2"));
    assertEquals(responseBody, response.getBody());

    verify(artifactServiceSpy, times(artifact.getTags().size()))
        .removeTag(eq(artifact.getArtifactId()), anyString());
    verify(pythonClientService, times(1)).processTags(anyList());
    verify(tagService, times(2)).createTag(any(TagCreateDto.class));
  }

  /**
   * Tests an execution of the processTags method when the artifact is not found.
   */
  @Test
  public void testProcessTagsArtifactNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the processTags method when the user is not found.
   */
  @Test
  public void testProcessTagsUserNotFound() {
    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the processTags method when the user is not the owner of the project.
   */
  @Test
  public void testProcessTagsUserNotOwner() {
    // Arrange
    project.setOwnerId("6798e2740b80b85362a8ba92");
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    // Act
    ResponseEntity<?> response = artifactService.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to view this artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the processTags method when there is an error removing tags 
   * from artifact.
   */
  @Test
  public void testProcessTagsErrorRemovingTags() {
    // Arrange
    ArtifactService artifactServiceSpy = Mockito.spy(artifactService);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(artifactRepository.save(artifact)).thenReturn(artifact);

    doReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
        .when(artifactServiceSpy).removeTag(eq(artifact.getArtifactId()), anyString());

    // Act
    ResponseEntity<?> response = artifactServiceSpy.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Error removing tags from artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the processTags method when there is an error processing tags.
   */
  @Test
  public void testProcessTagsErrorProcessingTags() {
    // Arrange
    ArtifactService artifactServiceSpy = Mockito.spy(artifactService);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(artifactRepository.save(artifact)).thenReturn(artifact);

    when(pythonClientService.processTags(anyList())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactServiceSpy.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Error processing tags.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the processTags method when there is an error processing JSON.
   */
  @Test
  public void testProcessTagsErrorProcessingJsonInvalidJson() {
    // Arrange
    ArtifactService artifactServiceSpy = Mockito.spy(artifactService);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    doReturn(ResponseEntity.ok().build()).when(artifactServiceSpy).removeTag(anyString(),
        anyString());

    when(pythonClientService.processTags(anyList())).thenReturn("invalid-json");

    // Act
    ResponseEntity<?> response = artifactService.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    assertNotNull(response.getBody());
    String responseBody = response.getBody().toString();
    // Ensure it contains the base error message
    assertTrue(responseBody.contains("Error processing JSON."));

    // Ensure it contains an exception message
    assertTrue(responseBody.contains("Unrecognized token")
        || responseBody.contains("Unexpected character"));
  }

  /**
   * Tests an execution of the processTags method when there is an error creating tags.
   */
  @Test
  public void testProcessTagsErrorCreatingTagsFromProcessedTags() {
    // Arrange
    ArtifactService artifactServiceSpy = Mockito.spy(artifactService);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    doReturn(ResponseEntity.ok().build()).when(artifactServiceSpy).removeTag(anyString(),
        anyString());

    when(pythonClientService.processTags(anyList()))
        .thenReturn("{ \"result\": [\"processedTag1\", \"processedTag2\"] }");

    when(tagService.createTag(any(TagCreateDto.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    // Act
    ResponseEntity<?> response = artifactService.processTags(artifact.getArtifactId());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Error creating tags from processed tags.");
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests a successful execution of the starTagging(List) method.
   */
  @Test
  public void testStartTaggingList() {
    Artifact artifactToTag =
        new Artifact("artifactToTagName", "projectId", "teamId", mockFilePath.toString());
    artifact.setArtifactId("6754740c8d6446369ba02b62");
    List<String> artifacts = new ArrayList<>();
    artifacts.add(artifact.getArtifactId());
    artifacts.add(artifactToTag.getArtifactId());
    artifact.setTaggingOpen(false);
    artifactToTag.setTaggingOpen(false);

    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(artifactRepository.findArtifactByArtifactId(artifactToTag.getArtifactId()))
        .thenReturn(artifactToTag);
    when(artifactRepository.save(artifact)).thenReturn(artifact);
    when(artifactRepository.save(artifactToTag)).thenReturn(artifactToTag);

    // Act
    ResponseEntity<?> response = artifactService.startTagging(artifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tagging started successfully for all artifacts.");
    assertTrue(artifact.isTaggingOpen());
    assertTrue(artifactToTag.isTaggingOpen());
    verify(artifactRepository, times(1)).save(artifact);
    verify(artifactRepository, times(1)).save(artifactToTag);
  }

  /**
   * Tests an execution of the startTagging(List) method when the artifactId provided is empty.
   */
  @Test
  public void testStartTaggingListEmptyArtifactId() {
    // Arrange
    List<String> emptyArtifacts = new ArrayList<>();

    // Act
    ResponseEntity<?> response = artifactService.startTagging(emptyArtifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the startTagging(List) method when the artifactId is null.
   */
  @Test
  public void testStartTaggingListNullArtifactId() {
    // Arrange
    List<String> nullArtifacts = null;

    // Act
    ResponseEntity<?> response = artifactService.startTagging(nullArtifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the startTagging(List) method when the artifact is not found.
   */
  @Test
  public void testStartTaggingListArtifactNotFound() {
    // Arrange
    List<String> artifacts = new ArrayList<>(List.of(artifact.getArtifactId()));
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.startTagging(artifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
  }

  /**
   * Tests a successful execution of the stopTagging(List) method.
   */
  @Test
  public void testStopTaggingList() {
    Artifact artifactToTag =
        new Artifact("artifactToTagName", "projectId", "teamId", mockFilePath.toString());
    artifactToTag.setArtifactId("6754740c8d6446369ba02b62");
    artifact.setTaggingOpen(true);
    artifactToTag.setTaggingOpen(true);
    List<String> artifacts = new ArrayList<>();
    artifacts.add(artifact.getArtifactId());
    artifacts.add(artifactToTag.getArtifactId());

    // Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(artifactRepository.findArtifactByArtifactId(artifactToTag.getArtifactId()))
        .thenReturn(artifactToTag);
    when(artifactRepository.save(artifact)).thenReturn(artifact);
    when(artifactRepository.save(artifactToTag)).thenReturn(artifactToTag);

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(artifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tagging stopped successfully for all artifacts.");
    assertFalse(artifact.isTaggingOpen());
    assertFalse(artifactToTag.isTaggingOpen());
    verify(artifactRepository, times(1)).save(artifact);
    verify(artifactRepository, times(1)).save(artifactToTag);
  }

  /**
   * Tests an execution of the stopTagging(List) method when the artifactId provided is empty.
   */
  @Test
  public void testStopTaggingListEmptyArtifactId() {
    // Arrange
    List<String> emptyArtifacts = new ArrayList<>();

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(emptyArtifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the stopTagging(List) method when the artifactId is null.
   */
  @Test
  public void testStopTaggingListNullArtifactId() {
    // Arrange
    List<String> nullArtifacts = null;

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(nullArtifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the stopTagging(List) method when the artifact is not found.
   */
  @Test
  public void testStopTaggingListArtifactNotFound() {
    // Arrange
    List<String> artifacts = new ArrayList<>(List.of(artifact.getArtifactId()));
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).thenReturn(null);

    // Act
    ResponseEntity<?> response = artifactService.stopTagging(artifacts);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
  }


}
