package it.unisannio.studenti.qualitag.dto.project;

import java.util.List;

/**
 * DTO used for project creation in the system.
 */
public record WholeProjectDto(
        String projectName,
        String projectDescription,
        long projectCreationDate,
        long projectDeadline,
        String ownerId,
        String projectStatus,
        List<String> users,
        List<String> artifacts,
        List<String> teams) {

}
