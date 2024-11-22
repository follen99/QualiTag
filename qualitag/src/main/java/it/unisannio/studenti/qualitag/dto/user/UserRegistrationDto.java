package it.unisannio.studenti.qualitag.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO used for user registration in the system.
 */
public record UserRegistrationDto(
    @NotBlank @Size(min = 1, message = "Username must not be blank") String username,
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String name,
    @NotBlank String surname) {

}