package it.unisannio.studenti.qualitag.dto.artifact;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * DTO used for artifact creation in the system.
 */
public record WholeArtifactDto(
        @NotBlank String artifactId,
        @NotBlank String artifactName,
        @NotBlank String content,
        List<String> tags) {
}


