package it.unisannio.studenti.qualitag.dto.project;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

/**
 * DTO used for project creation in the system.
 *
 */
public record ProjectCreationDto(
    @NotBlank String projectName,
    @NotBlank String projectDescription,
    @NotBlank Date creationDate,
    @NotBlank Date deadlineDate) {

}
