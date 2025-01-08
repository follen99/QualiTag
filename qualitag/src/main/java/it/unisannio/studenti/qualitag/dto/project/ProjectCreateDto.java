package it.unisannio.studenti.qualitag.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO used for project creation in the system.
 */
public record ProjectCreateDto(
    @NotBlank String projectName,
    @NotBlank String projectDescription,
    @NotBlank String deadlineDate,
    @NotEmpty List<String> userEmails) {

}
