package it.unisannio.studenti.qualitag.dto.tag;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for creating a new tag in the system.
 */
public record TagUpdateDto(
    @NotBlank String tagValue, 
    @NotBlank String colorHex) {

}
