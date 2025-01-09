package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.model.Project;

/**
 * Mapper for the Project entity.
 * Provides methods to convert between Project entities and ProjectDTO.
 */
public class ProjectMapper {

  /**
   * Converts a CompletedProjectCreationDto to a Project entity.
   *
   * @param dto The CompletedProjectCreationDto to convert
   * @return The converted Project entity
   */
  public static Project toEntity(CompletedProjectCreationDto dto) {
    if (dto == null) {
      return null;
    }
    return new Project(
        dto.projectName(),
        dto.projectDescription(),
        dto.projectCreationDate(),
        dto.projectDeadline(),
        dto.ownerId(),
        dto.userIds()
    );
  }
}
