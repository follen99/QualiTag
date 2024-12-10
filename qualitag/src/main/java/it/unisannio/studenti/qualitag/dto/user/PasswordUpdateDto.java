package it.unisannio.studenti.qualitag.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for updating the password of a user.
 */
public record PasswordUpdateDto(
    @NotBlank String newPassword,
    @NotBlank String confirmPassword) {

}
