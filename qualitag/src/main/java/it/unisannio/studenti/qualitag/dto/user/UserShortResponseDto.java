package it.unisannio.studenti.qualitag.dto.user;

import java.util.Map;

/**
 * Represents the User in the system to be displayed without sensitive data like ID and hashed
 * password.
 */
public record UserShortResponseDto(
    String username,
    String email,
    String name,
    String surname,
    Map<String, String> projectRoles
) {

}
