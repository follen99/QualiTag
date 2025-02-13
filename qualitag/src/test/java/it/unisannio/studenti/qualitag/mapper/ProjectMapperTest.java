package it.unisannio.studenti.qualitag.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.model.Project;
import java.time.Instant;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjectMapperTest {

  private Project entity;
  private CompletedProjectCreationDto dto;
  private ProjectMapper projectMapper;

  /**
   * Initializes the test environment.
   */
  @BeforeEach
  public void setUp() {
    projectMapper = new ProjectMapper();
    long creationDate = Instant.now().toEpochMilli();
    long deadline = Instant.parse("2025-12-31T23:59:59Z").toEpochMilli();
    entity = new Project("projectName",
        "projectDescription", creationDate, deadline,
        "6998e2740b87d85362a8ba58", new ArrayList<>());
    dto = new CompletedProjectCreationDto("projectName",
        "projectDescription",
        creationDate, deadline, "6998e2740b87d85362a8ba58", new ArrayList<>());
  }

  /**
   * Tests the toEntity method of the ProjectMapper class.
   */
  @Test
  public void testToEntity() {
    Project result = projectMapper.toEntity(dto);
    assertEquals(entity.getProjectName(), result.getProjectName());
    assertEquals(entity.getProjectDescription(), result.getProjectDescription());
    assertEquals(entity.getProjectCreationDate(), result.getProjectCreationDate());
    assertEquals(entity.getProjectDeadline(), result.getProjectDeadline());
    assertEquals(entity.getOwnerId(), result.getOwnerId());
    assertEquals(entity.getUserIds(), result.getUserIds());
  }

  /**
   * Tests the toEntity method of the ProjectMapper class with a null
   */
  @Test
  public void testToEntityWithNull() {
    Project result = projectMapper.toEntity(null);
    assertNull(result);
  }


}
