package it.unisannio.studenti.qualitag.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for artifact creation in the system.
 */
public record ArtifactCreationDto(@NotBlank String content) {

}
