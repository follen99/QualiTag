package it.unisannio.studenti.qualitag.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.model.Artifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for the ArtifactRepository class.
 */
class ArtifactRepositoryTest {

  @Mock
  private ArtifactRepository artifactRepository;

  // @InjectMocks
  //private ArtifactRepositoryTest artifactRepositoryTest;

  private Artifact artifact;

  /**
   * Set up the repository and an artifact for testing.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    artifact = new Artifact("artifactName1", "projectId1",
        "teamId1", "filePath1");
  }

  /**
   * Test the findArtifactByArtifactId method.
   */
  @Test
  void testFindArtifactByArtifactId() {
    when(artifactRepository.findArtifactByArtifactId("artifactId1")).thenReturn(artifact);
    Artifact foundArtifact = artifactRepository.findArtifactByArtifactId("artifactId1");
    assertEquals(artifact, foundArtifact);
  }

  /**
   * Test the findArtifactByArtifactId method when the artifact is not found.
   */
  @Test
  void testFindArtifactByArtifactIdNotFound() {
    when(artifactRepository.findArtifactByArtifactId("artifactId2")).thenReturn(null);
    Artifact foundArtifact = artifactRepository.findArtifactByArtifactId("artifactId2");
    assertNull(foundArtifact);
  }

  /**
   * Test the delete artifact by id method.
   */
  @Test
  void testDeleteArtifactByArtifactId() {
    artifactRepository.deleteArtifactByArtifactId("artifactId1");
    when(artifactRepository.findArtifactByArtifactId("artifactId1")).thenReturn(null);
    Artifact foundArtifact = artifactRepository.findArtifactByArtifactId("artifactId1");
    assertNull(foundArtifact);
  }
}