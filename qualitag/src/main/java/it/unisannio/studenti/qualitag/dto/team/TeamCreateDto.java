package it.unisannio.studenti.qualitag.dto.team;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record TeamCreateDto(
    @NotBlank String teamName,
    @NotBlank String projectId,
    @NotBlank Long creationDate,
    @NotBlank String teamDescription,
    List<String> users) {
}
