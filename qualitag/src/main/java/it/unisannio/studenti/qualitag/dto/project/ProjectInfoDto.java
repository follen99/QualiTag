package it.unisannio.studenti.qualitag.dto.project;

import java.util.List;

/**
 * DTO used for project creation in the system.
 */
public record ProjectInfoDto(
        String projectName,
        String projectDescription,
        String projectStatus,
        String projectId) {
}
