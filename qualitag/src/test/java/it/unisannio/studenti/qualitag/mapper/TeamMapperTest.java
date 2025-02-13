package it.unisannio.studenti.qualitag.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.unisannio.studenti.qualitag.dto.team.CompletedTeamCreateDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
import it.unisannio.studenti.qualitag.model.Team;
import java.time.Instant;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TeamMapperTest {

  private Team entity;
  private CompletedTeamCreateDto dto;
  private WholeTeamDto wholeTeamDto;

  /**
   * Initializes the test environment.
   */
  @BeforeEach
  public void setUp() {
    long creationTimestamp = Instant.now().toEpochMilli();
    entity = new Team("teamName", "projectId",
        creationTimestamp, "teamDescription",
        new ArrayList<>());
    entity.setTeamId("teamId");
    dto = new CompletedTeamCreateDto("teamName", "projectId",
        creationTimestamp, "teamDescription", new ArrayList<>());
    wholeTeamDto = new WholeTeamDto("teamId", "projectId",
        new ArrayList<>(), "teamName",
        Instant.now().toEpochMilli(), "teamDescription");
  }

  /**
   * Tests the constructor of the TeamMapper class.
   */
  @Test
  public void testConstructor() {
    new TeamMapper();
  }

  /**
   * Tests the toEntity method of the TeamMapper class.
   */
  @Test
  public void testToEntity() {
    Team result = TeamMapper.toEntity(dto);
    assertEquals(entity.getTeamName(), result.getTeamName());
    assertEquals(entity.getProjectId(), result.getProjectId());
    assertEquals(entity.getCreationTimeStamp(), result.getCreationTimeStamp());
    assertEquals(entity.getTeamDescription(), result.getTeamDescription());
    assertEquals(entity.getUserIds(), result.getUserIds());
  }

  /**
   * Tests the toEntity method of the TeamMapper class when the dto is null
   */
  @Test
  public void testToEntityWithNull() {
    CompletedTeamCreateDto dto = null;
    Team result = TeamMapper.toEntity(dto);
    assertNull(result);
  }

  /**
   * Tests the toWholeTeamDto method of the TeamMapper class.
   */
  @Test
  public void testToWholeTeamDto() {
    WholeTeamDto result = TeamMapper.toWholeTeamDto(entity);
    assertEquals(wholeTeamDto.teamId(), result.teamId());
    assertEquals(wholeTeamDto.projectId(), result.projectId());
    assertEquals(wholeTeamDto.users(), result.users());
    assertEquals(wholeTeamDto.teamName(), result.teamName());
    assertEquals(wholeTeamDto.creationTimeStamp(), result.creationTimeStamp());
    assertEquals(wholeTeamDto.teamDescription(), result.teamDescription());
  }


}
