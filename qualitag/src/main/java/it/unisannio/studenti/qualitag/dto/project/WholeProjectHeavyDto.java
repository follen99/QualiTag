package it.unisannio.studenti.qualitag.dto.project;

import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDTO;

import java.util.List;

/**
 * DTO used for project creation in the system.
 */
public record WholeProjectHeavyDto(
        String projectName,
        String projectDescription,
        long projectCreationDate,
        long projectDeadline,
        UserShortResponseDTO owner,
        String projectStatus,
        List<UserShortResponseDTO> users,
        List<WholeArtifactDto> artifacts,
        List<WholeTeamDto> teams) {

}
