package it.unisannio.studenti.qualitag.dto.artifact;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO used for adding tags to an artifact in the system.
 */
public record AddTagsToArtifactDto(
    @NotEmpty List<String> tagIds) {
    
}
