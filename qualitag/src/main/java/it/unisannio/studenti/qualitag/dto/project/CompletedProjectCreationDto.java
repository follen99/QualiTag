package it.unisannio.studenti.qualitag.dto.project;

import java.util.List;

/**
 * DTO used for project creation in the system.
 */
public record CompletedProjectCreationDto(
    String projectName,
    String projectDescription,
    long projectCreationDate,
    long projectDeadline,
    String ownerId,
    List<String> userIds) {

}
