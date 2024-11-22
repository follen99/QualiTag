package it.unisannio.studenti.qualitag.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for user registration in the system.
 */
public record UserRegistrationDto(
    @NotBlank String username,
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String name,
    @NotBlank String surname) {

}