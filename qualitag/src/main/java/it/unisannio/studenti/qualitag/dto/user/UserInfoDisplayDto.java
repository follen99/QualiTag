package it.unisannio.studenti.qualitag.dto.user;

import it.unisannio.studenti.qualitag.model.Role;
import java.util.List;
import java.util.Map;

/**
 * Represents the User in the system to be displayed without sensitive data like ID and hashed
 * password.
 */
public record UserInfoDisplayDto(
    String username,
    String email,
    String name,
    String surname,
    List<String> projectIds,
    List<String> teamIds,
    List<String> tagIds,
    Map<String, Role> projectRoles
) {

}
