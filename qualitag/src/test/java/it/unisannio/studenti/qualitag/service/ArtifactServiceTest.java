package it.unisannio.studenti.qualitag.service;


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
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
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
  private Artifact artifact;
  private Project project;
  private User user;
  private Team team;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    artifactCreateDto = new ArtifactCreateDto("artifactName",
        "projectId", "teamId", file);
    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    project = new Project("projectName", "projectDescription",
        0L, 0L, "ownerId", new ArrayList<>());
    user = new User("username", "user@example.com",
        "hashedPassword123", "John", "Doe");
    team = new Team("TeamName", "projectId",
        123456789L, "Description",
        new ArrayList<>(Arrays.asList("user1", "user2")));
  }

}