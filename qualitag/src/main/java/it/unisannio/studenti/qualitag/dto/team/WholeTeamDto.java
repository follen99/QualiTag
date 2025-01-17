package it.unisannio.studenti.qualitag.dto.team;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * DTO used for passing the whole team information in the system.
 */
public record WholeTeamDto(
        @NotBlank String teamId,
        @NotBlank String projectId,
        @NotBlank List<String> users,
        @NotBlank String teamName,
        @NotBlank Long creationTimeStamp,
        @NotBlank String teamDescription) {

}
