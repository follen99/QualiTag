package it.unisannio.studenti.qualitag.dto.user;


import java.util.List;
import java.util.Map;

/**
 * Represents the User in the system to be displayed without sensitive data like ID and hashed
 * password.
 */
public record UserResponseDto(
        String username,
        String email,
        String name,
        String surname,
        List<String> projectIds,
        List<String> teamIds,
        List<String> tagIds,
        Map<String, String> projectRoles) {

}
