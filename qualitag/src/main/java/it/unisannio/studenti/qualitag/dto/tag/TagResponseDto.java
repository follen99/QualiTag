package it.unisannio.studenti.qualitag.dto.tag;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for creating a new tag in the system.
 */
public record TagResponseDto(
    @NotBlank String tagId,
    @NotBlank String tagValue,
    @NotBlank String createdBy,
    @NotBlank String colorHex) {

}
