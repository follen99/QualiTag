package it.unisannio.studenti.qualitag.dto.project;

/**
 * DTO used for project creation in the system.
 */
public record ProjectInfoDto(
        String projectName,
        String projectDescription,
        String projectStatus,
        String projectId) {
}
