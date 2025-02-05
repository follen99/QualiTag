package it.unisannio.studenti.qualitag.service;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertEquals;


import it.unisannio.studenti.qualitag.dto.artifact.AddTagsToArtifactDto;
import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
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
  private AddTagsToArtifactDto addTagsToArtifactDto;
  private Artifact artifact;
  private Project project;
  private User user1, user2, owner, otherUser;
  private Team team;
  private Tag tag1, tag2, tag3, tag4;

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

    mockFilePath = Paths.get("/mock/path/to/file.txt");

    artifact = new Artifact("artifactName",
        "projectId", "teamId", mockFilePath.toString());
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
    artifactCreateDto = new ArtifactCreateDto(artifact.getArtifactName(),
        artifact.getDescription(), project.getProjectId(), file);
    wholeArtifactDto =
        new WholeArtifactDto(artifact.getArtifactId(),
            artifact.getArtifactName(), artifact.getDescription(), artifact.getProjectId(),
            artifact.getTeamId(), artifact.getFilePath(), artifact.getTags(),
            artifact.isTaggingOpen());
    addTagsToArtifactDto =
        new AddTagsToArtifactDto(new ArrayList<>(Arrays.asList(tag3.getTagId(), tag4.getTagId())));

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
  public void testGetArtifact_Success() {
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
   * Tests an execution of the getArtifact method when the user is not authorized to view the
   * artifact
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
  public void testGetArtifact_IOException() {
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

  /**
   * Tests a successful execution of the deleteArtifact method
   */
  @Test
  public void testDeleteArtifact_Success() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
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

      //Act
      ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

      //Assert
      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Artifact deleted successfully.");
      assertEquals(responseBody, response.getBody());

      //Verify that the artifact has been removed from the project, team, and tags
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
   * Tests an execution of the deleteArtifact method when the artifactId provided is empty
   */
  @Test
  public void testDeleteArtifact_EmptyArtifactId() {
    //Arrange
    String emptyArtifactId = "";

    //Act
    ResponseEntity<?> response = artifactService.deleteArtifact(emptyArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the artifactId is null
   */
  @Test
  public void testDeleteArtifact_NullArtifactId() {
    //Arrange
    String nullArtifactId = null;

    //Act
    ResponseEntity<?> response = artifactService.deleteArtifact(nullArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the artifact is not found
   */
  @Test
  public void testDeleteArtifact_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the project is not found
   */
  @Test
  public void testDeleteArtifact_ProjectNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Project not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the user is not authorized
   */
  @Test
  public void testDeleteArtifact_UserNotAuthorized() {
    //Arrange
    project.setOwnerId("6798e2740b80b85362a8ba92");
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);

    //Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not the project owner.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the team is not found
   */
  @Test
  public void testDeleteArtifact_TeamNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Team not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when the file deletion fails
   */
  @Test
  public void testDeleteArtifact_FileDeletionFailure() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(projectRepository.save(project)).thenReturn(project);
    when(teamRepository.save(team)).thenReturn(team);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(tagRepository.save(tag1)).thenReturn(tag1);
    when(tagRepository.save(tag2)).thenReturn(tag2);

    //Act
    ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "File deletion failed.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteArtifact method when deletion of the artifact fails
   */
  @Test
  public void testDeleteArtifact_ArtifactDeletionFailure() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
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

      //Act
      ResponseEntity<?> response = artifactService.deleteArtifact(artifact.getArtifactId());

      //Assert
      assertNotNull(response);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Artifact not deleted.");
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests a successful execution of the removeTags method
   */
  @Test
  public void testRemoveTags_Success() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    //when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(teamRepository.save(team)).thenReturn(team);
    when(artifactRepository.save(artifact)).thenReturn(artifact);

    //Act
    ResponseEntity<?> response = artifactService.
        removeTag(artifact.getArtifactId(), tag1.getTagId());

    //Assert
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
   * Tests an execution of the removeTags method when the artifactId provided is empty
   */
  @Test
  public void testRemoveTags_EmptyArtifactId() {
    //Arrange
    String emptyArtifactId = "";

    //Act
    ResponseEntity<?> response = artifactService.removeTag(emptyArtifactId, tag1.getTagId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the artifactId is null
   */
  @Test
  public void testRemoveTags_NullArtifactId() {
    //Arrange
    String nullArtifactId = null;

    //Act
    ResponseEntity<?> response = artifactService.removeTag(nullArtifactId, tag1.getTagId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the tagId provided is empty
   */
  @Test
  public void testRemoveTags_EmptyTagId() {
    //Arrange
    String emptyTagId = "";

    //Act
    ResponseEntity<?> response = artifactService.removeTag(artifact.getArtifactId(), emptyTagId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the tagId is null
   */
  @Test
  public void testRemoveTags_NullTagId() {
    //Arrange
    String nullTagId = null;

    //Act
    ResponseEntity<?> response = artifactService.removeTag(artifact.getArtifactId(), nullTagId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the artifact is not found
   */
  @Test
  public void testRemoveTags_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.
        removeTag(artifact.getArtifactId(), tag1.getTagId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the logged-in user is not the owner of the
   * project
   */
  @Test
  public void testRemoveTags_UserNotOwner() {
    //Arrange
    project.setOwnerId("6798e2740b80b85362a8ba92");

    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    //Act
    ResponseEntity<?> response = artifactService.
        removeTag(artifact.getArtifactId(), tag1.getTagId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to remove this tag from the artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the removeTags method when the tag is not found in the artifact
   */
  @Test
  public void testRemoveTags_TagNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    //Act
    ResponseEntity<?> response = artifactService.
        removeTag(artifact.getArtifactId(), tag3.getTagId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found in artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the addTags method where the logged-in user is the project
   * owner
   */
  @Test
  public void testAddTags_SuccessfulOwner() {
    //Assert
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
    when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
    when(artifactRepository.save(artifact)).thenReturn(artifact);
    when(tagRepository.save(tag3)).thenReturn(tag3);
    when(tagRepository.save(tag4)).thenReturn(tag4);

    //Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    System.out.println(response.getBody());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags added successfully.");
    assertEquals(responseBody, response.getBody());

    assertTrue(artifact.getTags().contains(tag3.getTagId()));
    assertTrue(artifact.getTags().contains(tag4.getTagId()));
    //artifact is saved twice since we add two new tags
    verify(artifactRepository, times(2)).save(artifact);
    assertTrue(tag3.getArtifactIds().contains(artifact.getArtifactId()));
    verify(tagRepository, times(1)).save(tag3);
    assertTrue(tag4.getArtifactIds().contains(artifact.getArtifactId()));
    verify(tagRepository, times(1)).save(tag4);
  }

//  /**
//   * Tests a successful execution of the addTags method where the logged-in user is a team member
//   */
//  @Test
//  public void testAddTags_SuccessfulMember(){
//    //Arrange
//    Authentication authentication = mock(Authentication.class);
//    when(authentication.isAuthenticated()).thenReturn(true);
//    when(authentication.getName()).thenReturn("user1");
//    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user1));
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//
//    when(artifactRepository.findArtifactByArtifactId
//        (artifact.getArtifactId())).thenReturn(artifact);
//    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
//    when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
//    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
//    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
//    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
//
//    //Act
//    ResponseEntity<?> response =
//        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);
//
//    System.out.println(response.getBody());
//
//    //Assert
//    assertNotNull(response);
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    Map<String, Object> responseBody = new HashMap<>();
//    responseBody.put("msg", "Tags added successfully.");
//    assertEquals(responseBody, response.getBody());
//
//    assertTrue(artifact.getTags().contains(tag3.getTagId()));
//    assertTrue(artifact.getTags().contains(tag4.getTagId()));
//    //artifact is saved twice since we add two new tags
//    verify(artifactRepository, times(2)).save(artifact);
//    assertTrue(tag3.getArtifactIds().contains(artifact.getArtifactId()));
//    verify(tagRepository, times(1)).save(tag3);
//    assertTrue(tag4.getArtifactIds().contains(artifact.getArtifactId()));
//    verify(tagRepository, times(1)).save(tag4);
//  }

  /**
   * Tests an execution of the addTags method when the dto provided is invalid
   */
  @Test
  public void testAddTags_InvalidDto() {
    //Arrange
    AddTagsToArtifactDto invalidDto = new AddTagsToArtifactDto(new ArrayList<>());

    //Act
    ResponseEntity<?> response = artifactService.addTags(artifact.getArtifactId(), invalidDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid data.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the artifactId provided is empty
   */
  @Test
  public void testAddTags_EmptyArtifactId() {
    //Arrange
    String emptyArtifactId = "";

    //Act
    ResponseEntity<?> response = artifactService.addTags(emptyArtifactId, addTagsToArtifactDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the artifactId is null
   */
  @Test
  public void testAddTags_NullArtifactId() {
    //Arrange
    String nullArtifactId = null;

    //Act
    ResponseEntity<?> response = artifactService.addTags(nullArtifactId, addTagsToArtifactDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the artifact is not found
   */
  @Test
  public void testAddTags_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when one of the tagIds provided is empty
   */
  @Test
  public void testAddTags_EmptyTagId() {
    //Arrange
    AddTagsToArtifactDto invalidDto = new AddTagsToArtifactDto(
        new ArrayList<>(Arrays.asList("", "")));
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);

    //Act
    ResponseEntity<?> response = artifactService.addTags(artifact.getArtifactId(), invalidDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when one of the tagIds provided is null
   */
  @Test
  public void testAddTags_NullTagId() {
    //Arrange
    AddTagsToArtifactDto invalidDto = new AddTagsToArtifactDto(
        new ArrayList<>(Arrays.asList(null, null)));
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);

    //Act
    ResponseEntity<?> response = artifactService.addTags(artifact.getArtifactId(), invalidDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the tag is not found
   */
  @Test
  public void testAddTags_TagNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(null);

    //Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when one of the tags is already in the artifact
   */
  @Test
  public void testAddTags_TagAlreadyInArtifact() {
    //Arrange
    AddTagsToArtifactDto addPresentTagsDto = new AddTagsToArtifactDto(
        new ArrayList<>(Arrays.asList(tag1.getTagId(), tag4.getTagId())));
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addPresentTagsDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag already exists in artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the user is not the owner of th project
   */
  @Test
  public void testAddTags_UserNotOwner() {
    //Arrange
    project.setOwnerId("6798e2740b80b85362a8ba92");
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
    when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
    when(userRepository.findByUserId(owner.getUserId())).thenReturn(owner);
    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    //Act
    ResponseEntity<?> response =
        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not the project owner.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTags method when the user is not authorized to add tags
   */
//  @Test
//  public void testAddTags_UserNotAuthorized(){
//    //Arrange
//    Authentication authentication = mock(Authentication.class);
//    when(authentication.isAuthenticated()).thenReturn(true);
//    when(authentication.getName()).thenReturn("user2");
//    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user2));
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//
//    when(artifactRepository.findArtifactByArtifactId
//        (artifact.getArtifactId())).thenReturn(artifact);
//    when(tagRepository.findTagByTagId(tag3.getTagId())).thenReturn(tag3);
//    when(tagRepository.findTagByTagId(tag4.getTagId())).thenReturn(tag4);
//    when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);
//    when(projectRepository.findProjectByProjectId(artifact.getProjectId())).thenReturn(project);
//    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);
//
//    //Act
//    ResponseEntity<?> response =
//        artifactService.addTags(artifact.getArtifactId(), addTagsToArtifactDto);
//
//    //Assert
//    assertNotNull(response);
//    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    Map<String, Object> responseBody = new HashMap<>();
//    responseBody.put("msg", "User is not authorized to add tags to this artifact.");
//    assertEquals(responseBody, response.getBody());
//  }

  /**
   * Tests a successful execution of the getTagsByUser method (By using its ID)
   */
  @Test
  public void testGetTagsByUserId_Success() {
    //Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(projectRepository.findProjectByProjectId
        (artifact.getProjectId())).thenReturn(project);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    //Act
    ResponseEntity<?> response = artifactService.getTagsByUser(artifact.getArtifactId(),
        user1.getUserId());

    System.out.println(response.getBody());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags retrieved successfully.");
    responseBody.put("tags", Arrays.asList(tag1, tag2));
    assertEquals(responseBody, response.getBody());
  }


  /**
   * Tests a successful execution of the getTagsByUser method (By using its username)
   */
  @Test
  public void testGetTagsByUsername_Success() {
    //Arrange
    when(userRepository.findByUsername(user1.getUsername())).thenReturn(user1);
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(projectRepository.findProjectByProjectId
        (artifact.getProjectId())).thenReturn(project);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    //Act
    ResponseEntity<?> response = artifactService.getTagsByUser(artifact.getArtifactId(),
        user1.getUsername());

    System.out.println(response.getBody());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags retrieved successfully.");
    responseBody.put("tags", Arrays.asList(tag1, tag2));
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getTagsByUser (by using his email)
   */
//  @Test
//  public void testGetTagsByEmail_Success() {
//    //Arrange
//    when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
//    when(artifactRepository.findArtifactByArtifactId
//        (artifact.getArtifactId())).thenReturn(artifact);
//    when(projectRepository.findProjectByProjectId
//        (artifact.getProjectId())).thenReturn(project);
//    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
//    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
//
//    //Act
//    ResponseEntity<?> response = artifactService.getTagsByUser(artifact.getArtifactId(),
//        user1.getEmail());
//
//    System.out.println(response.getBody());
//
//    //Assert
//    assertNotNull(response);
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    Map<String, Object> responseBody = new HashMap<>();
//    responseBody.put("msg", "Tags retrieved successfully.");
//    responseBody.put("tags", Arrays.asList(tag1, tag2));
//    assertEquals(responseBody, response.getBody());
//  }

  /**
   * Tests an execution of the getTagsByUser method when the user is not found
   */
  @Test
  public void testGetTagsByUser_UserNotFound() {
    //Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getTagsByUser(artifact.getArtifactId(),
        user1.getUserId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the artifact is not found
   */
  @Test
  public void testGetTagsByUser_ArtifactNotFound() {
    //Arrange
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getTagsByUser(artifact.getArtifactId(),
        user1.getUserId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the user is not authorized
   */
  @Test
  public void testGetTagsByUser_UserNotAuthorized() {
    //Arrange
    when(userRepository.findByUserId(otherUser.getUserId())).thenReturn(otherUser);
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);
    when(projectRepository.findProjectByProjectId
        (artifact.getProjectId())).thenReturn(project);
    when(teamRepository.findTeamByTeamId(artifact.getTeamId())).thenReturn(team);

    //Act
    ResponseEntity<?> response = artifactService.getTagsByUser(artifact.getArtifactId(),
        otherUser.getUserId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User is not authorized to view this artifact.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getAllTags method
   */
  @Test
  public void testGetAllTags_Success() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);

    //Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    //Assert
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
   * Tests an execution of the getAllTags method when the artifactId is empty
   */
  @Test
  public void testGetAllTags_EmptyArtifactId() {
    //Arrange
    String emptyArtifactId = "";

    //Act
    ResponseEntity<?> response = artifactService.getAllTags(emptyArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the artifactId is null
   */
  @Test
  public void testGetAllTags_NullArtifactId() {
    //Arrange
    String nullArtifactId = null;

    //Act
    ResponseEntity<?> response = artifactService.getAllTags(nullArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the artifact is not found
   */
  @Test
  public void testGetAllTags_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the tag is not found
   */
  @Test
  public void testGetAllTags_TagNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getAllTags method when the user is not found
   */
  @Test
  public void testGetAllTags_UserNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(userRepository.findByUserId(user1.getUserId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.getAllTags(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the stopTagging method
   */
  @Test
  public void testStopTagging_Successful() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);

    //Act
    ResponseEntity<?> response = artifactService.
        stopTagging(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tagging stopped successfully.");
    assertEquals(responseBody, response.getBody());
    assertFalse(artifact.isTaggingOpen());
  }

  /**
   * Tests an execution of the stopTagging method when the artifactId provided is empty
   */
  @Test
  public void testStopTagging_EmptyArtifactId() {
    //Arrange
    String emptyArtifactId = "";

    //Act
    ResponseEntity<?> response = artifactService.stopTagging(emptyArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the stopTagging method when the artifactId is null
   */
  @Test
  public void testStopTagging_NullArtifactId() {
    //Arrange
    String nullArtifactId = null;

    //Act
    ResponseEntity<?> response = artifactService.stopTagging(nullArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the stopTagging method when the artifact is not found
   */
  @Test
  public void testStopTagging_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.stopTagging(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the startTagging method
   */
  @Test
  public void testStartTagging_Successful() {
    //Arrange
    artifact.setTaggingOpen(false);
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(artifact);

    //Act
    ResponseEntity<?> response = artifactService.
        startTagging(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tagging started successfully.");
    assertEquals(responseBody, response.getBody());
    assertTrue(artifact.isTaggingOpen());
  }

  /**
   * Tests an execution of the startTagging method when the artifactId provided is empty
   */
  @Test
  public void testStartTagging_EmptyArtifactId() {
    //Arrange
    String emptyArtifactId = "";

    //Act
    ResponseEntity<?> response = artifactService.startTagging(emptyArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the startTagging method when the artifactId is null
   */
  @Test
  public void testStartTagging_NullArtifactId() {
    //Arrange
    String nullArtifactId = null;

    //Act
    ResponseEntity<?> response = artifactService.startTagging(nullArtifactId);

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the startTagging method when the artifact is not found
   */
  @Test
  public void testStartTagging_ArtifactNotFound() {
    //Arrange
    when(artifactRepository.findArtifactByArtifactId
        (artifact.getArtifactId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = artifactService.startTagging(artifact.getArtifactId());

    //Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Artifact not found.");
    assertEquals(responseBody, response.getBody());
  }


}