package it.unisannio.studenti.qualitag.dto.project;

import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
import java.util.List;

/**
 * DTO used for project creation in the system.
 */
public record WholeProjectHeavyDto(
        String projectId,
        String projectName,
        String projectDescription,
        long projectCreationDate,
        long projectDeadline,
        UserShortResponseDto owner,
        String projectStatus,
        List<UserShortResponseDto> users,
        List<WholeArtifactDto> artifacts,
        List<WholeTeamDto> teams) {

}
