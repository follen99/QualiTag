package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.user.ProjectCreationDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
import it.unisannio.studenti.qualitag.model.Project;


/**
 * Mapper for the Project entity.
 * Provides methods to convert between Project entites and ProjectDTO.
 */
public class ProjectMapper {

  private final ProjectService projectService;

  /**
   * Construcs a new ProjectMapper
   *
   * @param projectService The project service to use
   */
  public ProjectMapper(ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Converts a ProjectCreationDto to a Project entity
   *
   * @param dto The ProjectCreationDto to convert
   * @return The converted Project entity
   */
  public Project toEntity(ProjectCreationDto dto) {
    if (dto == null) {
      return null;
    }
    return new Project(
        dto.projectName(),
        dto.projectDescription(),
        dto.deadlineDate()
    );
  }

  public ProjectCreationDto toDto(Project entity) {
    if (entity == null) {
      return null;
    }
    return new ProjectCreationDto(
        entity.getProjectName(),
        entity.getProjectDescription(),
        entity.getProjectCreationDate(),
        entity.getProjectDeadline()
    );
  }


}
