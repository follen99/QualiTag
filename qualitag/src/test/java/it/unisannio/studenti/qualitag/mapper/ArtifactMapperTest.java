package it.unisannio.studenti.qualitag.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.model.Artifact;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArtifactMapperTest {

  private Artifact entity;
  private ArtifactCreateDto dto;
  private WholeArtifactDto wholeArtifactDto;

  /**
   * Initializes the test environment.
   */
  @BeforeEach
  public void setUp() {

    Path mockFilePath = Paths.get("src/test/resources/test.txt");
    entity = new Artifact("artifactName", "description",
        "projectId", "teamId", mockFilePath.toString());
    entity.setArtifactId("6754705c8d6446369ca02b62");

    dto = new ArtifactCreateDto("artifactName", "description",
        "projectId", "teamId", null);
    wholeArtifactDto = new WholeArtifactDto("6754705c8d6446369ca02b62",
        "artifactName", "description", "projectId", "teamId",
        mockFilePath.toString(), new ArrayList<>(), true);
  }

  /**
   * Tests the constructor of the ArtifactMapper class.
   */
  @Test
  public void testConstructor() {
    new ArtifactMapper();
  }

  /**
   * Tests the toEntity method of the ArtifactMapper class.
   */
  @Test
  public void testToEntity() {
    Artifact result = ArtifactMapper.toEntity(dto);
    assertEquals(entity.getArtifactName(), result.getArtifactName());
    assertEquals(entity.getDescription(), result.getDescription());
    assertEquals(entity.getProjectId(), result.getProjectId());
    assertEquals(entity.getTeamId(), result.getTeamId());
  }

  /**
   * Tests the toWholeArtifactDto method of the ArtifactMapper class.
   */
  @Test
  public void testToWholeArtifactDto() {
    WholeArtifactDto result = ArtifactMapper.toWholeArtifactDto(entity);
    assertEquals(wholeArtifactDto.artifactId(), result.artifactId());
    assertEquals(wholeArtifactDto.artifactName(), result.artifactName());
    assertEquals(wholeArtifactDto.description(), result.description());
    assertEquals(wholeArtifactDto.projectId(), result.projectId());
    assertEquals(wholeArtifactDto.teamId(), result.teamId());
    assertEquals(wholeArtifactDto.filePath(), result.filePath());
    assertEquals(wholeArtifactDto.tags(), result.tags());
    assertEquals(wholeArtifactDto.isTaggingOpen(), result.isTaggingOpen());
  }

}
