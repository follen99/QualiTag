package it.unisannio.studenti.qualitag.dto.artifact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO used for artifact creation in the system.
 */
public record ArtifactCreateDto(
    @NotBlank String artifactName,
    String description,
    @NotBlank String projectId,
    String teamId,
    @NotNull MultipartFile file) {
}
