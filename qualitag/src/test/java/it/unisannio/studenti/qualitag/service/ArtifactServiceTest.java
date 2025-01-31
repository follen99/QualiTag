package it.unisannio.studenti.qualitag.service;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagUpdateDto;
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
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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
  private ArtifactMapper artifactMapper;
  private Artifact artifact;
  private Project project;
  private User user1, user2, owner;
  private Team team;
  private Tag tag1, tag2;


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
        "password2", "Alice", "Smith");
    owner.setUserId("ownerId");

    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    artifact.setArtifactId("6754705c8d6446369ca02b62");
    artifact.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId()
        , tag2.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(Arrays.asList(artifact.getArtifactId())));
    tag2.setArtifactIds(new ArrayList<>(Arrays.asList(artifact.getArtifactId())));

    project = new Project("projectName", "projectDescription",
        0L, 0L, "ownerId", new ArrayList<>());
    project.setProjectId("projectId");
    project.setUserIds(new ArrayList<>(Arrays.asList("ownerId", "user1Id", "user2Id")));
    user1.setProjectIds(new ArrayList<>(Arrays.asList("projectId")));
    owner.setProjectIds(new ArrayList<>(Arrays.asList("projectId")));

    team = new Team("teamName", "projectId",
        123456789L, "teamDescription",
        new ArrayList<>(Arrays.asList("user1Id", "user2Id")));
    team.setTeamId("teamId");
    user1.setTeamIds(new ArrayList<>(Arrays.asList("teamId")));
    user2.setTeamIds(new ArrayList<>(Arrays.asList("teamId")));

    //Initialize the DTO
    artifactCreateDto = new ArtifactCreateDto("artifactName",
        "artifactDescription", project.getProjectId(), file);

    //Initialize authorization details
    // Mock SecurityContextHolder to provide an authenticated user
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("user1");
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user1));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

//  /**
//   * Tests a successfull execution of the addArtifact method.
//   */
//  @Test
//  public void testAddArtifact_Success() throws IOException {
//    //Arrange
//    when(projectRepository.findProjectByProjectId(artifactCreateDto.projectId()))
//        .thenReturn(project);
//    when(userRepository.findByUserId(project.getOwnerId())).thenReturn(owner);
//  }

}