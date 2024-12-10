package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
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
   * @param correctTeamDto The TeamCreateDto to convert.
   * @return The converted Team entity, or null if the input is null.
   */
  public Team toEntity(TeamCreateDto correctTeamDto) {
    if (correctTeamDto == null) {
      return null;
    }

    return new Team(correctTeamDto.teamName(), correctTeamDto.creationDate(),
        correctTeamDto.teamDescription(), correctTeamDto.users());
  }
}
