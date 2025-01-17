package it.unisannio.studenti.qualitag.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO used for creating a new team in the system.
 */
public record TeamCreateDto(
        @NotBlank String teamName,
        String teamDescription,
        @NotBlank String projectId,
        @NotEmpty List<String> userEmails) {

}
