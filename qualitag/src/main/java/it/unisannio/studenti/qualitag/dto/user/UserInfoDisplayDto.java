package it.unisannio.studenti.qualitag.dto.user;

import it.unisannio.studenti.qualitag.model.Role;
import java.util.List;
import java.util.Map;

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
