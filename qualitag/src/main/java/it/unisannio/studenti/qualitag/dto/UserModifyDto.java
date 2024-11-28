package it.unisannio.studenti.qualitag.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for modifying a user in the system.
 */
public record UserModifyDto(
    @NotBlank String username,
    @NotBlank String email,
    @NotBlank String name,
    @NotBlank String surname) {

}
