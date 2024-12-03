package it.unisannio.studenti.qualitag.dto.team;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO used for creating a new team in the system.
 */
public record TeamCreateDto(@NotBlank String teamName, @NotBlank Long creationDate,
                            @NotBlank String teamDescription, List<String> users) {

}
