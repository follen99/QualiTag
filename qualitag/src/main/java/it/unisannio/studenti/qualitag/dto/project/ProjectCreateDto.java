package it.unisannio.studenti.qualitag.dto.project;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * DTO used for project creation in the system.
 *
 */
public record ProjectCreateDto(
    @NotBlank String projectName,
    @NotBlank String projectDescription,
    @NotBlank Long creationDate,
    @NotBlank Long deadlineDate,
    List<String> users,
    List <String> teams,
    List<String> artifacts,
    String ownerId) {

}
