package it.unisannio.studenti.qualitag.dto.artifact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO used for adding tags to an artifact in the system.
 */
public record AddTagsToArtifactDto(
    @NotBlank String artifactId,
    @NotEmpty List<String> tagIds) {
    
}
