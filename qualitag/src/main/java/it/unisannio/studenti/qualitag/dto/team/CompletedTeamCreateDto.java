package it.unisannio.studenti.qualitag.dto.team;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO used for creating a new team in the system.
 */
public record CompletedTeamCreateDto(
    @NotBlank String teamName,
    @NotBlank String projectId,
    @NotBlank Long creationDate,
    @NotBlank String teamDescription,
    List<String> userIds) {

}
