package it.unisannio.studenti.qualitag.dto.project;

import java.util.List;

/**
 * DTO used for project update in the system.
 */
public record CompletedProjectUpdateDto(
    String projectName,
    String projectDescription,
    long projectDeadline,
    List<String> userIds) {
        
}
