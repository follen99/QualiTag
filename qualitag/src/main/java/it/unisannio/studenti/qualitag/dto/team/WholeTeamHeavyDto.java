package it.unisannio.studenti.qualitag.dto.team;

import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectInfoDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO used for passing the whole team information in the system.
 */
public record WholeTeamHeavyDto(
        @NotBlank String teamId,
        @NotBlank ProjectInfoDto project,
        @NotBlank List<UserShortResponseDto> users,
        List<WholeArtifactDto> artifacts,
        @NotBlank String teamName,
        @NotBlank Long creationTimeStamp,
        @NotBlank String teamDescription) {

}
