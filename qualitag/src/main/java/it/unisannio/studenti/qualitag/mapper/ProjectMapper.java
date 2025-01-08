package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.service.ProjectService;


/**
 * Mapper for the Project entity.
 * Provides methods to convert between Project entities and ProjectDTO.
 */
public class ProjectMapper {

  private final ProjectService projectService;

  /**
   * Constructs a new ProjectMapper.
   *
   * @param projectService The project service to use
   */
  public ProjectMapper(ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Converts a CompletedProjectCreationDto to a Project entity.
   *
   * @param dto The CompletedProjectCreationDto to convert
   * @return The converted Project entity
   */
  public Project toEntity(CompletedProjectCreationDto dto) {
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
