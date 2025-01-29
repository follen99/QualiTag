package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.team.CompletedTeamCreateDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
import it.unisannio.studenti.qualitag.model.Team;
import org.springframework.stereotype.Component;

/**
 * Mapper class for the Team entity.
 */
@Component
public class TeamMapper {

  /**
   * Converts a TeamCreateDto to a Team entity.
   *
   * @param correctTeamDto The CompletedTeamCreateDto to convert.
   * @return The converted Team entity, or null if the input is null.
   */
  public static Team toEntity(CompletedTeamCreateDto correctTeamDto) {
    if (correctTeamDto == null) {
      return null;
    }

    return new Team(
        correctTeamDto.teamName(),
        correctTeamDto.projectId(),
        correctTeamDto.creationDate(),
        correctTeamDto.teamDescription(),
        correctTeamDto.userIds()
    );
  }

  /**
   * Convert to DTO.
   *
   * @return The DTO
   */
  public static WholeTeamDto toWholeTeamDto(Team team) {
    return new WholeTeamDto(
        team.getTeamId(),
        team.getProjectId(),
        team.getUserIds(),
        team.getTeamName(),
        team.getCreationTimeStamp(),
        team.getTeamDescription()
    );
  }
}
