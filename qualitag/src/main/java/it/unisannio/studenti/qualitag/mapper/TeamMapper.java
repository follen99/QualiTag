package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.model.Team;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

  public Team toEntity(TeamCreateDto correctTeamDto) {
    if (correctTeamDto == null) {
      return null;
    }

    return new Team(
        correctTeamDto.projectId(),
        correctTeamDto.teamName(),
        correctTeamDto.creationDate(),
        correctTeamDto.teamDescription(),
        correctTeamDto.users());
  }
}
